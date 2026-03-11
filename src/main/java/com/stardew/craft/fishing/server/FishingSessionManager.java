package com.stardew.craft.fishing.server;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.tool.FishingRodItem;
import com.stardew.craft.fishing.network.FishingCatchVisualPayload;
import com.stardew.craft.fishing.network.FishingFailVisualPayload;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.SkillType;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.FishingHook;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class FishingSessionManager {
	private static final Map<MinecraftServer, FishingSessionManager> BY_SERVER = new ConcurrentHashMap<>();

	public static FishingSessionManager get(MinecraftServer server) {
		return BY_SERVER.computeIfAbsent(server, s -> new FishingSessionManager());
	}

	public static void tickAllServers() {
		for (FishingSessionManager mgr : BY_SERVER.values()) {
			mgr.tick();
		}
	}

	private final Map<UUID, FishingSession> sessionsByPlayer = new HashMap<>();

	private FishingSessionManager() {
	}

	public boolean start(ServerPlayer player, float castPower01) {
		if (!isHoldingStardewFishingRod(player)) {
			return false;
		}

		UUID playerId = player.getUUID();
		FishingSession existing = sessionsByPlayer.get(playerId);
		if (existing != null && existing.state() != FishingSession.State.DONE) {
			return false;
		}

		// Stardew Valley timing (StardewValley.Tools.FishingRod.calculateTimeUntilFishingBite)
		int fishingLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.FISHING);
		RandomSource random = player.getRandom();
		final int minFishingBiteTimeMs = 600;
		final int maxFishingBiteTimeMs = 30000;
		int ticksUntilBite;

		// 获取鱼竿
		ItemStack rod = player.getMainHandItem();
		if (rod.isEmpty() || !(rod.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem)) {
			rod = player.getOffhandItem();
		}
		
		if (rod != null && rod.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem fishingRodItem) {
			// Spinner & Dressed Spinner: reduce the MAX bite time by 5s/10s each (stackable).
			int reductionTimeMs = 0;
			reductionTimeMs += com.stardew.craft.item.tool.FishingRodItem.countTackle(rod, "stardewcraft:dressed_spinner") * 10000;
			reductionTimeMs += com.stardew.craft.item.tool.FishingRodItem.countTackle(rod, "stardewcraft:spinner") * 5000;

			int maxExclusiveMs = Math.max(minFishingBiteTimeMs, maxFishingBiteTimeMs - 250 * fishingLevel - reductionTimeMs);
			int timeMs;
			if (maxExclusiveMs <= minFishingBiteTimeMs) {
				timeMs = minFishingBiteTimeMs;
			} else {
				timeMs = minFishingBiteTimeMs + random.nextInt(maxExclusiveMs - minFishingBiteTimeMs);
			}

			// SV: first cast is faster (we don't model nibble loops, so treat casts as "first").
			timeMs = (int) (timeMs * 0.75f);

			// SV: any bait halves the bite time, with additional multipliers for specific baits.
			ItemStack baitStack = fishingRodItem.getAttachmentsForTooltip(rod).bait();
			if (!baitStack.isEmpty()) {
				timeMs = (int) (timeMs * 0.5f);
				if (com.stardew.craft.item.tool.FishingRodItem.hasBait(rod, "stardewcraft:wild_bait")
						|| com.stardew.craft.item.tool.FishingRodItem.hasBait(rod, "stardewcraft:challenge_bait")) {
					timeMs = (int) (timeMs * 0.75f);
				} else if (com.stardew.craft.item.tool.FishingRodItem.hasBait(rod, "stardewcraft:deluxe_bait")) {
					timeMs = (int) (timeMs * 0.66f);
				}
			}

			timeMs = Math.max(500, timeMs);
			ticksUntilBite = Math.max(10, (int) Math.ceil(timeMs / 50.0));
		} else {
			// Fallback: if something goes wrong, keep a sane default.
			ticksUntilBite = 20 * 2;
		}

		// Placeholder pos/depth; the session will update them once the hook actually lands in water.
		FishingSession session = new FishingSession(UUID.randomUUID(), player.blockPosition(), 1, ticksUntilBite);

		// 使用原版 FishingHook：直接获得原版鱼线/鱼钩渲染。
		FishingHook hook = spawnVanillaHook(player, castPower01);
		if (hook != null) {
			session.setHookEntityId(hook.getId());
		}

		sessionsByPlayer.put(playerId, session);
		return true;
	}

	private static ItemStack getRodFromPlayer(ServerPlayer player) {
		ItemStack rod = player.getMainHandItem();
		if (rod.isEmpty() || !(rod.getItem() instanceof FishingRodItem)) {
			rod = player.getOffhandItem();
		}
		if (rod.isEmpty() || !(rod.getItem() instanceof FishingRodItem)) {
			return ItemStack.EMPTY;
		}
		return rod;
	}

	@SuppressWarnings("null")
	public boolean tryStartMinigame(ServerPlayer player) {
		FishingSession session = sessionsByPlayer.get(player.getUUID());
		if (session == null) {
			return false;
		}
		if (session.state() != FishingSession.State.BITE_READY) {
			return false;
		}
		if (!isHoldingStardewFishingRod(player)) {
			return false;
		}
		if (!isHookAlive(player.serverLevel(), session)) {
			return false;
		}

		// SV: non-fish catchables don't trigger the minigame.
		if (session.skipMinigame()) {
			finishInstantCatch(player, session);
			return true;
		}

		// Play the "hooked" animation first, then open the minigame.
		// Match FishingCatchVisuals HOOKED popup duration (450ms) => 9 ticks.
		final int hookedAnimTicks = 9;
		session.startHookedAnim(hookedAnimTicks);
		PacketDistributor.sendToPlayer(player, new com.stardew.craft.fishing.network.FishingHookedAnimPayload(hookedAnimTicks));
		player.serverLevel().playSound(null, player.blockPosition(), ModSounds.FISH_HIT.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
		return true;
	}

	@SuppressWarnings({ "null", "unused" })
	private boolean startMinigameNow(ServerPlayer player, FishingSession session) {
		// 进入小游戏（原版没有倒计时；这里用 -1 表示不超时）。
		session.startMinigame(-1);
		
		// 计算渔具效果（星露谷原版逻辑）
		ItemStack rod = getRodFromPlayer(player);
		int barSizeBonus = 0;  // 浮标大小加成（像素）
		float escapeLossPerTick = 0.003f;
		int barbedHookCount = 0;
		int leadBobberCount = 0;
		int corkBobberCount = 0;
		boolean hasSonarBobber = false;
		String sonarFishItemId = "";
		
		if (rod != null && rod.getItem() instanceof FishingRodItem rodItem) {
			// Cork Bobber: 每个+24像素（可叠加）
			corkBobberCount = FishingRodItem.countTackle(rod, "stardewcraft:cork_bobber");
			barSizeBonus += corkBobberCount * 24;

			// Deluxe Bait: +12像素（原版 BobberBar 构造直接加）
			if (FishingRodItem.hasBait(rod, "stardewcraft:deluxe_bait")) {
				barSizeBonus += 12;
			}
			
			// Trap Bobber (SV): reduction starts at 0.003 and decreases by 0.001, then halves each extra.
			// Clamp minimum to 0.001.
			int trapBobberCount = FishingRodItem.countTackle(rod, "stardewcraft:trap_bobber");
			float reduction = 0.003f;
			float amount = 0.001f;
			for (int i = 0; i < trapBobberCount; i++) {
				reduction -= amount;
				amount /= 2f;
				if (reduction < 0.001f) {
					reduction = 0.001f;
					break;
				}
			}
			escapeLossPerTick = reduction;

			// Barbed Hook & Lead Bobber counts (client-side physics)
			barbedHookCount = FishingRodItem.countTackle(rod, "stardewcraft:barbed_hook");
			leadBobberCount = FishingRodItem.countTackle(rod, "stardewcraft:lead_bobber");

			// Sonar Bobber: shows hooked fish before it's caught
			hasSonarBobber = FishingRodItem.hasTackle(rod, "stardewcraft:sonar_bobber");
			if (hasSonarBobber) {
				ItemStack planned = session.plannedCatch();
				if (planned != null && !planned.isEmpty()) {
					sonarFishItemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(planned.getItem()).toString();
				}
			}
		}
		
		PacketDistributor.sendToPlayer(player, new com.stardew.craft.fishing.network.StartMinigamePayload(
				session.id(),
				session.difficulty(),
				session.motionTypeId(),
				session.ticksUntilTimeout(),
				session.hasTreasure(),
				session.isGoldenTreasure(),
				hasSonarBobber,
				sonarFishItemId,
				barSizeBonus,
				escapeLossPerTick,
				barbedHookCount,
				leadBobberCount
		));
		return true;
	}

	@SuppressWarnings("null")
	private void finishInstantCatch(ServerPlayer player, FishingSession session) {
		// Mirror the "caught" branch but without minigame, treasure, or tackle durability consumption.
		ItemStack rod = getRodFromPlayer(player);
		ItemStack caughtStack = session.plannedCatch();
		if (caughtStack == null || caughtStack.isEmpty()) {
			caughtStack = new ItemStack(com.stardew.craft.item.ModItems.TRASH.get());
		}

		@SuppressWarnings("null")
		boolean added = player.getInventory().add(caughtStack.copy());
		if (!added) {
			player.drop(caughtStack.copy(), false);
		}

		player.playNotifySound(ModSounds.PULL_ITEM_FROM_WATER.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
		player.playNotifySound(ModSounds.DWOP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);

		@SuppressWarnings("null")
		var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(caughtStack.getItem());
		PacketDistributor.sendToPlayer(player, new FishingCatchVisualPayload(id, caughtStack.getCount()));

		// SV: non-fish items grant 3 fishing XP.
		PlayerStardewDataAPI.addExperience(player, SkillType.FISHING, 3);

		// Consume bait on success, but do NOT consume tackle durability for junk/catchables.
		com.stardew.craft.item.tool.FishingRodItem.consumeBait(player, rod);

		cleanupHook(player.serverLevel(), session);
		session.finish();
		sessionsByPlayer.remove(player.getUUID());
		clearAllRodCastFlags(player);
	}

	public FishingSession.State getState(ServerPlayer player) {
		FishingSession session = sessionsByPlayer.get(player.getUUID());
		if (session == null) {
			return null;
		}
		if (session.state() == FishingSession.State.DONE) {
			return null;
		}
		return session.state();
	}

	public boolean cancel(ServerPlayer player) {
		FishingSession session = sessionsByPlayer.remove(player.getUUID());
		if (session == null) {
			return false;
		}
		cleanupHook(player.serverLevel(), session);
		session.finish();
		clearAllRodCastFlags(player);
		return true;
	}

	@SuppressWarnings("null")
	public void handleResult(ServerPlayer player, UUID sessionId, boolean success, float catchProgress, boolean treasureCaught, int numCaught) {
		if (!isHoldingStardewFishingRod(player)) {
			// 只允许手持本模组钓竿的玩家结算小游戏，防止伪包/换手作弊。
			success = false;
		}

		if (!Float.isFinite(catchProgress)) {
			success = false;
			catchProgress = -1f;
		}

		FishingSession session = sessionsByPlayer.get(player.getUUID());
		if (session == null) {
			return;
		}
		if (!session.id().equals(sessionId)) {
			return;
		}
		if (session.state() != FishingSession.State.MINIGAME) {
			return;
		}

		// Stardew: distanceFromCatching >= 1 => caught; <= 0 => escaped.
		boolean caught = success && catchProgress >= 1.0f;
		
		// 获取鱼竿（用于消耗鱼饵和渔具）
		ItemStack rod = player.getMainHandItem();
		if (rod.isEmpty() || !(rod.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem)) {
			rod = player.getOffhandItem();
		}
		boolean hasWildBait = rod != null && !rod.isEmpty() && com.stardew.craft.item.tool.FishingRodItem.hasBait(rod, "stardewcraft:wild_bait");
		boolean hasChallengeBait = rod != null && !rod.isEmpty() && com.stardew.craft.item.tool.FishingRodItem.hasBait(rod, "stardewcraft:challenge_bait");
		
		if (caught) {
			// SV: Wild Bait can catch 2 fish (25% + dailyLuck/2).
			int finalNumCaught = 1;
			if (hasChallengeBait) {
				finalNumCaught = Mth.clamp(numCaught, 1, 3);
			} else if (hasWildBait) {
				double dailyLuck = PlayerStardewDataAPI.getDailyLuck(player);
				double chance = 0.25 + dailyLuck / 2.0;
				chance = Mth.clamp(chance, 0.0, 1.0);
				if (player.getRandom().nextDouble() < chance) {
				finalNumCaught = 2;
				}
			}

			ItemStack fish = session.plannedCatch();
			boolean hasPlannedCatch = !fish.isEmpty();
			if (fish.isEmpty()) {
				// 如果plannedCatch为空（理论上不应该发生），给玩家垃圾而不是COD
				StardewCraft.LOGGER.warn("Player {} caught fish but plannedCatch is empty! Giving trash.", player.getName().getString());
				fish = new ItemStack(com.stardew.craft.item.ModItems.TRASH.get());
			} else {
				// 计算鱼的最终品质（星露谷原版逻辑）
				int fishQuality = session.initialQuality();  // 初始品质（0-2）
				boolean wasPerfect = (catchProgress >= 1.0f);  // 完美捕获（进度条100%）
				
				// 1. Quality Bobber效果：每个+1品质等级
				if (rod != null && rod.getItem() instanceof FishingRodItem) {
					if (FishingRodItem.hasTackle(rod, "stardewcraft:quality_bobber")) {
						fishQuality++;
						// 品质上限是3（铱星）
						if (fishQuality > 3) {
							fishQuality = 3;
						}
					}
				}
				
				// 2. Perfect效果：额外提升品质
				if (fishQuality >= 2 && wasPerfect) {
					fishQuality = 3;  // 金星 → 铱星
				} else if (fishQuality >= 1 && wasPerfect) {
					fishQuality = 2;  // 银星 → 金星
				}
				
				// 确保品质在0-3范围内
				fishQuality = Math.max(0, Math.min(3, fishQuality));
				
				// 使用QualityHelper设置品质
				com.stardew.craft.item.quality.QualityHelper.setQuality(fish, fishQuality);
			}

			if (hasPlannedCatch) {
				@SuppressWarnings("null")
				String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(fish.getItem()).toString();
				PlayerStardewDataAPI.addFishCatchCount(player, itemId, finalNumCaught);
			}
			
			// Give multiple fish if applicable.
			if (finalNumCaught <= 1) {
				@SuppressWarnings("null")
				boolean added = player.getInventory().add(fish.copy());
				if (!added) {
					player.drop(fish.copy(), false);
				}
			} else {
				int max = Math.max(1, fish.getMaxStackSize());
				int remaining = finalNumCaught;
				while (remaining > 0) {
					int give = Math.min(max, remaining);
					ItemStack stack = fish.copy();
					stack.setCount(give);
					boolean added = player.getInventory().add(stack);
					if (!added) {
						player.drop(stack, false);
					}
					remaining -= give;
				}
			}

			// 处理宝箱战利品（在鱼动画之前发送，让客户端知道有宝箱）
			if (session.hasTreasure() && treasureCaught) {
				generateAndGiveTreasure(player, session);
			}

			// Success SFX: 使用 playNotifySound 确保玩家自己能听到
			player.playNotifySound(ModSounds.PULL_ITEM_FROM_WATER.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
			player.playNotifySound(ModSounds.DWOP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
			// 喜庆音效 - 钓鱼成功时播放
			player.playNotifySound(ModSounds.JINGLE1.get(), net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 1.0f);

			// Client-side post-catch visuals (SV-style popup then item-activation).
			@SuppressWarnings("null")
			var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(fish.getItem());
			PacketDistributor.sendToPlayer(player, new FishingCatchVisualPayload(id, fish.getCount()));
			
			int baseExp = 10 + session.difficulty();
			PlayerStardewDataAPI.addExperience(player, SkillType.FISHING, baseExp);
			
			// 消耗鱼饵（成功时消耗 1；若有 Preserving 则 50% 概率不消耗）
			com.stardew.craft.item.tool.FishingRodItem.consumeBait(player, rod);
			
			// SV: junk/non-fish catchables don't consume tackle durability.
			if (!session.skipMinigame()) {
				com.stardew.craft.item.tool.FishingRodItem.consumeTackleDurability(rod);
			}
		} else {
			PlayerStardewDataAPI.addExperience(player, SkillType.FISHING, 2);
			// Client-side failure feedback (short visual).
			PacketDistributor.sendToPlayer(player, new FishingFailVisualPayload());

			// 失败时也消耗鱼饵（SV：若有 Preserving 则 50% 概率保留；Deluxe Bait 不影响消耗）
			com.stardew.craft.item.tool.FishingRodItem.consumeBait(player, rod);
		}

		// Avoid leaving the vanilla hook/line around after the result.
		cleanupHook(player.serverLevel(), session);
		session.finish();
		sessionsByPlayer.remove(player.getUUID());
		clearAllRodCastFlags(player);
	}

	@SuppressWarnings("null")
	private void tick() {
		Iterator<Map.Entry<UUID, FishingSession>> it = sessionsByPlayer.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, FishingSession> entry = it.next();
			UUID playerId = entry.getKey();
			FishingSession session = entry.getValue();
			ServerPlayer player = getAnyPlayer(playerId);
			if (player == null) {
				it.remove();
				continue;
			}
			if (!isHoldingStardewFishingRod(player)) {
				// 玩家不再持竿（切换物品/死亡等）：直接取消会话。
				StardewCraft.LOGGER.info("[TICK DEBUG] Player not holding Stardew rod, cancelling session");
				cleanupHook(player.serverLevel(), session);
				clearAllRodCastFlags(player);
				it.remove();
				continue;
			}
			ServerLevel level = player.serverLevel();
			if (!isHookAlive(level, session)) {
				StardewCraft.LOGGER.info("[TICK DEBUG] Hook not alive (id={}), cancelling session. player.fishing={}", 
					session.hookEntityId(),
					player.fishing != null ? player.fishing.getId() + " alive=" + player.fishing.isAlive() : "null");
				clearAllRodCastFlags(player);
				it.remove();
				continue;
			}
			RandomSource random = player.getRandom();
			FishingSession.State before = session.state();
			boolean stillActive = session.tick(player, level, random);
			if (!stillActive) {
				cleanupHook(level, session);
				clearAllRodCastFlags(player);
				it.remove();
				continue;
			}

			if (before == FishingSession.State.WAITING_BITE && session.state() == FishingSession.State.BITE_READY) {
				// Bite prompt: show a clear visual cue (exclamation + bobber dip) without chat spam.
				int hookId = session.hookEntityId();
				PacketDistributor.sendToPlayer(player, new com.stardew.craft.fishing.network.FishingBitePromptPayload(hookId, 10));
				level.playSound(null, player.blockPosition(), pickFishBiteChime(player), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
			}
			if (session.state() == FishingSession.State.HOOKED_ANIM && session.ticksUntilTimeout() <= 0) {
				startMinigameNow(player, session);
			}
			if (session.state() == FishingSession.State.DONE) {
				cleanupHook(level, session);
				clearAllRodCastFlags(player);
				it.remove();
			}
		}
	}

	private static void clearAllRodCastFlags(ServerPlayer player) {
		// Ensure we don't leave the client in a stuck "cast" model state.
		boolean hadAny = false;
		var inv = player.getInventory();
		for (ItemStack s : inv.items) {
			if (s != null && !s.isEmpty() && s.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
				hadAny |= com.stardew.craft.item.tool.FishingRodItem.isCastActive(s);
				com.stardew.craft.item.tool.FishingRodItem.setCastActive(s, false);
			}
		}
		for (ItemStack s : inv.offhand) {
			if (s != null && !s.isEmpty() && s.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
				hadAny |= com.stardew.craft.item.tool.FishingRodItem.isCastActive(s);
				com.stardew.craft.item.tool.FishingRodItem.setCastActive(s, false);
			}
		}
		ItemStack main = player.getMainHandItem();
		if (!main.isEmpty() && main.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
			hadAny |= com.stardew.craft.item.tool.FishingRodItem.isCastActive(main);
			com.stardew.craft.item.tool.FishingRodItem.setCastActive(main, false);
		}
		ItemStack off = player.getOffhandItem();
		if (!off.isEmpty() && off.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem) {
			hadAny |= com.stardew.craft.item.tool.FishingRodItem.isCastActive(off);
			com.stardew.craft.item.tool.FishingRodItem.setCastActive(off, false);
		}
		if (hadAny) {
			net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new com.stardew.craft.fishing.network.FishingRodCastStatePayload(false));
		}
	}

	private static net.minecraft.sounds.SoundEvent pickFishBiteChime(ServerPlayer player) {
		// Stardew: Farmer.PlayFishBiteChime() picks one of 4 variants (fishBite or fishBite_alternate_0..2).
		// We emulate the variety deterministically per-player to keep it stable across a session.
		int idx = Math.floorMod(player.getUUID().hashCode(), 4);
		return switch (idx) {
			case 1 -> ModSounds.FISH_BITE_ALTERNATE_0.get();
			case 2 -> ModSounds.FISH_BITE_ALTERNATE_1.get();
			case 3 -> ModSounds.FISH_BITE_ALTERNATE_2.get();
			default -> ModSounds.FISH_BITE.get();
		};
	}

	private static boolean isHookAlive(ServerLevel level, FishingSession session) {
		int id = session.hookEntityId();
		if (id < 0) {
			return true;
		}
		var e = level.getEntity(id);
		if (e == null || !e.isAlive()) {
			session.setHookEntityId(-1);
			return false;
		}
		return true;
	}

	private static void cleanupHook(ServerLevel level, FishingSession session) {
		int id = session.hookEntityId();
		if (id < 0) {
			return;
		}
		var e = level.getEntity(id);
		if (e != null) {
			e.discard();
		}
		session.setHookEntityId(-1);
	}

	private static FishingHook spawnVanillaHook(ServerPlayer player, float castPower01) {
		ServerLevel level = player.serverLevel();
		try {
			// 1.20+ 常见构造：FishingHook(Player, Level, luck, lure)
			@SuppressWarnings("null")
			FishingHook hook = new FishingHook(player, level, 0, 0);
			// Let the hook fly out like vanilla; charge changes initial velocity => visible distance difference.
			float velocity = Mth.lerp(Mth.clamp(castPower01, 0f, 1f), 0.7f, 1.8f);
			hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
			level.addFreshEntity(hook);
			return hook;
		} catch (Throwable t) {
			StardewCraft.LOGGER.error("Failed to spawn vanilla FishingHook", t);
			return null;
		}
	}

	private static boolean isHoldingStardewFishingRod(ServerPlayer player) {
		return player.getMainHandItem().getItem() instanceof com.stardew.craft.item.tool.FishingRodItem
				|| player.getOffhandItem().getItem() instanceof com.stardew.craft.item.tool.FishingRodItem;
	}

	private ServerPlayer getAnyPlayer(UUID playerId) {
		for (MinecraftServer server : BY_SERVER.keySet()) {
			@SuppressWarnings("null")
			ServerPlayer player = server.getPlayerList().getPlayer(playerId);
			if (player != null) {
				return player;
			}
		}
		return null;
	}

	@SuppressWarnings("null")
	static int estimateWaterDepth(ServerLevel level, BlockPos pos, int maxRadius) {
		// 近似实现：在平面上找最近的“非水方块”，以曼哈顿距离作为离岸距离。
		int best = maxRadius + 1;
		for (int dx = -maxRadius; dx <= maxRadius; dx++) {
			for (int dz = -maxRadius; dz <= maxRadius; dz++) {
				int dist = Math.abs(dx) + Math.abs(dz);
				if (dist == 0 || dist >= best) {
					continue;
				}
				BlockPos p = pos.offset(dx, 0, dz);
				if (!level.getFluidState(p).is(net.minecraft.tags.FluidTags.WATER)) {
					best = dist;
				}
			}
		}
		return Math.max(1, Math.min(best, maxRadius));
	}

	/**
	 * 生成并给予玩家宝箱战利品 - 通过打开UI显示
	 */
	@SuppressWarnings("null")
	private void generateAndGiveTreasure(ServerPlayer player, FishingSession session) {
		com.stardew.craft.fishing.data.TreasureLootManager lootMgr = getLootManager();
		int fishingLevel = com.stardew.craft.player.PlayerStardewDataAPI.getSkillLevel(player, com.stardew.craft.player.SkillType.FISHING);
		double dailyLuck = PlayerStardewDataAPI.getDailyLuck(player);

		java.util.List<ItemStack> loot = lootMgr.generateTreasure(
				fishingLevel,
				session.isGoldenTreasure(),
				player.getRandom(),
				50,
				dailyLuck
		);

		// 保存战利品到session
		session.setTreasureLoot(loot);

		// 发送打开宝箱UI的网络包到客户端
		// 注意：UI会在鱼动画展示之后才打开，由客户端控制时机
		net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
				player, 
				new com.stardew.craft.network.payload.OpenTreasureChestPayload(loot, session.isGoldenTreasure())
		);

		// 播放宝箱打开音效
		player.playNotifySound(ModSounds.NEW_ARTIFACT.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
		StardewCraft.LOGGER.info("Player {} opened treasure chest with {} items (golden={})",
				player.getName().getString(), loot.size(), session.isGoldenTreasure());
	}

	/**
	 * 获取宝箱战利品管理器（需要从服务器资源管理器获取）
	 */
	private com.stardew.craft.fishing.data.TreasureLootManager getLootManager() {
		// 创建一个新的管理器并手动初始化默认数据
		com.stardew.craft.fishing.data.TreasureLootManager manager = new com.stardew.craft.fishing.data.TreasureLootManager();
		manager.initializeWithDefaults();  // 手动初始化默认数据
		return manager;
	}
}
