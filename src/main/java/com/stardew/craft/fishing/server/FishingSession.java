package com.stardew.craft.fishing.server;

import com.stardew.craft.fishing.data.FishingDataManager;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import com.stardew.craft.item.IStardewItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FishingSession {
	public enum State {
		WAITING_BITE,
		BITE_READY,
		HOOKED_ANIM,
		MINIGAME,
		DONE
	}

	// 星露谷物语的基础宝箱概率
	private static final double BASE_TREASURE_CHANCE = 0.15;

	private final UUID id;
	private BlockPos bobberPos;
	private int waterDepth;
	private State state;
	private int ticksUntilBite;
	private int ticksUntilTimeout;
	private ItemStack plannedCatch;
	private int difficulty;
	private int motionTypeId;
	private boolean skipMinigame;
	private int hookEntityId;
	private boolean hookInWater;
	private int settleTicks;

	// 宝箱相关
	private boolean hasTreasure;
	private boolean goldenTreasure;
	private List<ItemStack> treasureLoot;
	
	// 鱼品质相关
	private double fishSize;  // 0.0-1.0，决定初始品质
	private int initialQuality;  // 基础品质（0-2）

	public FishingSession(UUID id, BlockPos bobberPos, int waterDepth, int ticksUntilBite) {
		this.id = id;
		this.bobberPos = bobberPos;
		this.waterDepth = waterDepth;
		this.ticksUntilBite = ticksUntilBite;
		this.ticksUntilTimeout = 0;
		this.state = State.WAITING_BITE;
		this.plannedCatch = ItemStack.EMPTY;
		this.skipMinigame = false;
		this.hookEntityId = -1;
		this.hookInWater = false;
		this.settleTicks = 40;
		this.hasTreasure = false;
		this.goldenTreasure = false;
		this.treasureLoot = List.of();
		this.fishSize = 0.0;
		this.initialQuality = 0;
	}

	public UUID id() {
		return id;
	}

	public State state() {
		return state;
	}

	public int waterDepth() {
		return waterDepth;
	}

	public BlockPos bobberPos() {
		return bobberPos;
	}

	public int difficulty() {
		return difficulty;
	}

	public int motionTypeId() {
		return motionTypeId;
	}

	public int ticksUntilTimeout() {
		return ticksUntilTimeout;
	}

	public boolean skipMinigame() {
		return skipMinigame;
	}

	public int hookEntityId() {
		return hookEntityId;
	}

	public void setHookEntityId(int entityId) {
		this.hookEntityId = entityId;
	}

	public boolean hasTreasure() {
		return hasTreasure;
	}

	public boolean isGoldenTreasure() {
		return goldenTreasure;
	}

	public List<ItemStack> treasureLoot() {
		return treasureLoot;
	}

	public void setTreasureLoot(List<ItemStack> loot) {
		this.treasureLoot = loot;
	}

	@SuppressWarnings("null")
	public boolean tick(ServerPlayer player, ServerLevel level, RandomSource random) {
		if (state == State.DONE) {
			return false;
		}

		// Track hook landing. Only start the bite timer once the hook is actually in water.
		if (hookEntityId >= 0) {
			var e = level.getEntity(hookEntityId);
			if (e != null) {
				BlockPos hookPos = e.blockPosition();
				this.bobberPos = hookPos;
				boolean inWater = level.getFluidState(hookPos).is(net.minecraft.tags.FluidTags.WATER);
				if (inWater && !hookInWater) {
					hookInWater = true;
					this.waterDepth = com.stardew.craft.fishing.server.FishingSessionManager.estimateWaterDepth(level, hookPos, 8);
				}

				// If the hook fails to land in water soon, treat it as a cancelled/invalid cast (vanilla would reel back).
				if (!hookInWater) {
					settleTicks--;
					if (settleTicks <= 0) {
						state = State.DONE;
						return false;
					}
				}
			}
		}
		if (state == State.WAITING_BITE) {
			if (!hookInWater) {
				return true;
			}
			ticksUntilBite--;
			if (ticksUntilBite > 0) {
				return true;
			}

			// 使用最新的bobberPos进行鱼类选择（因为初始创建session时用的是player位置占位符）
			BlockPos actualBobberPos = (hookEntityId >= 0) ? this.bobberPos : bobberPos;
			Optional<FishingDataManager.FishSelection> selected = FishingDataManager.get().selectFish(player, level, actualBobberPos, waterDepth, random);
			if (selected.isEmpty()) {
				plannedCatch = ItemStack.EMPTY;
				difficulty = 15;
				motionTypeId = 0;
				skipMinigame = false;
				this.fishSize = 0.0;
				this.initialQuality = 0;
			} else {
				// 找到鱼了！计算鱼的大小和初始品质
				// fishSize受钓鱼等级影响：等级越高，鱼越大
				int fishingLevel = com.stardew.craft.player.PlayerStardewDataAPI.getSkillLevel(player, SkillType.FISHING);
				this.fishSize = random.nextDouble() * (0.2 + fishingLevel * 0.04);  // 0.0-1.0范围
				this.fishSize = Math.min(1.0, this.fishSize);  // 限制在1.0以内
				
				// 根据fishSize决定初始品质（星露谷原版逻辑）
				if (this.fishSize < 0.33) {
					this.initialQuality = 0;  // 普通
				} else if (this.fishSize < 0.66) {
					this.initialQuality = 1;  // 银星
				} else {
					this.initialQuality = 2;  // 金星
				}
				
				plannedCatch = selected.get().stack();
				difficulty = selected.get().difficulty();
				motionTypeId = selected.get().motionTypeId();
				skipMinigame = selected.get().skipMinigame();
			}

			// 决定是否生成宝箱(参考星露谷物语的计算)
			// 获取鱼竿用于检查鱼饵和渔具
			ItemStack rod = player.getMainHandItem();
			if (rod.isEmpty() || !(rod.getItem() instanceof com.stardew.craft.item.tool.FishingRodItem)) {
				rod = player.getOffhandItem();
			}
			decideTreasure(player, rod, random);

			// 咬钩了：等待玩家再次右键"收杆"才进入小游戏。
			state = State.BITE_READY;
			ticksUntilTimeout = 20 * 10;
			return true;
		}

		if (state == State.BITE_READY) {
			ticksUntilTimeout--;
			if (ticksUntilTimeout <= 0) {
				state = State.DONE;
				return false;
			}
			return true;
		}

		if (state == State.HOOKED_ANIM) {
			// Countdown until the server opens the minigame.
			// The manager will transition to MINIGAME when this reaches 0.
			ticksUntilTimeout--;
			return true;
		}

		if (state == State.MINIGAME) {
			if (ticksUntilTimeout < 0) {
				return true;
			}
			ticksUntilTimeout--;
			return ticksUntilTimeout > 0;
		}

		return false;
	}

	public void finish() {
		state = State.DONE;
	}

	public void startMinigame(int minigameTimeoutTicks) {
		state = State.MINIGAME;
		ticksUntilTimeout = minigameTimeoutTicks;
	}

	public void startHookedAnim(int animTicks) {
		state = State.HOOKED_ANIM;
		ticksUntilTimeout = animTicks;
	}

	public ItemStack plannedCatch() {
		return plannedCatch;
	}

	public boolean isPlannedCatchLegendaryFish() {
		if (plannedCatch == null || plannedCatch.isEmpty()) {
			return false;
		}
		if (!(plannedCatch.getItem() instanceof IStardewItem stardewItem)) {
			return false;
		}
		return "stardewcraft.type.legendary_fish".equals(stardewItem.getItemTypeKey());
	}
	
	public double fishSize() {
		return fishSize;
	}
	
	public int initialQuality() {
		return initialQuality;
	}

	/**
	 * 决定是否生成宝箱(参考星露谷物语FishingRod.cs的startMinigameEndFunction)
	 * 
	 * 原版逻辑：
	 * baseChanceForTreasure = 0.15
	 * chance = base + LuckLevel*0.005 + dailyLuck/2 + (profession_pirate ? base : 0) + (bait_magnet ? base : 0) + extraTackle
	 */
	private void decideTreasure(ServerPlayer player, ItemStack rod, RandomSource random) {
		// SV behavior: non-fish catchables that skip the minigame can't have treasure.
		if (skipMinigame) {
			hasTreasure = false;
			goldenTreasure = false;
			treasureLoot = List.of();
			return;
		}
		int fishingLevel = com.stardew.craft.player.PlayerStardewDataAPI.getSkillLevel(player, SkillType.FISHING);
		
		// 基础概率 15%
		double treasureChance = BASE_TREASURE_CHANCE;

		// 幸运Buff（SV: LuckLevel*0.005）
		int luckBuff = com.stardew.craft.player.PlayerStardewDataAPI.getLuckBuffLevel(player);
		treasureChance += luckBuff * 0.005;

		// 钓鱼等级加成：每级 +0.5%
		treasureChance += fishingLevel * 0.005;

		// 幸运加成：dailyLuck/2
		double dailyLuck = com.stardew.craft.player.PlayerStardewDataAPI.getDailyLuck(player);
		treasureChance += dailyLuck / 2.0;

		// Magnet鱼饵：+15% (物品ID: stardewcraft:magnet)
		if (com.stardew.craft.item.tool.FishingRodItem.hasBait(rod, "stardewcraft:magnet")) {
			treasureChance += BASE_TREASURE_CHANCE;
		}
		
		// Treasure Hunter渔具：每个+5%（原版按 tackleIds 计数叠加）
		int treasureHunterCount = com.stardew.craft.item.tool.FishingRodItem.countTackle(rod, "stardewcraft:treasure_hunter");
		if (treasureHunterCount > 0) {
			treasureChance += treasureHunterCount * (BASE_TREASURE_CHANCE / 3.0); // 0.15/3 = 0.05
		}

		// 投骰子
		hasTreasure = random.nextDouble() < treasureChance;

		// 如果有宝箱，判断是否为金色
		// SV(1.6): 需要 Fishing Mastery，且概率为 0.25 + AverageDailyLuck。
		// 本模组目前按 per-player daily luck 实现，因此用玩家 dailyLuck 近似 AverageDailyLuck。
		if (hasTreasure) {
			double goldenChance = 0.25 + dailyLuck;
			goldenChance = net.minecraft.util.Mth.clamp(goldenChance, 0.0, 1.0);
			if (fishingLevel >= 10 && random.nextDouble() < goldenChance) {
				goldenTreasure = true;
			}
		}
	}
}
