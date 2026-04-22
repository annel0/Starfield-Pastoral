package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.interior.CrossDimensionTeleporter;
import com.stardew.craft.interior.InteriorPortalRegistry;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.interior.PlayerInteriorAllocator;
import com.stardew.craft.mining.MineEntranceBootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class InteriorPortalInteractionEvents {

    private static final String TAG_TARGET_PREFIX = "sdv_portal_target:";

    private static final String PLAYER_FLAG_INTERIOR = "stardewcraft_interior_space";
    private static final String PLAYER_LAST_PORTAL_TICK = "stardewcraft_last_portal_tick";

    private static final long PORTAL_COOLDOWN_TICKS = 8L;

    // ======================== 旧实体兼容入口（已废弃，Interaction 实体由 InteriorSubspaceLifecycleEvents 拦截取消加载） ========================

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Entity target = event.getTarget();
        Optional<String> targetId = findTagValue(target.getTags(), TAG_TARGET_PREFIX);
        if (targetId.isEmpty()) return;

        handlePortalInteraction(player, targetId.get());
        event.setCanceled(true);
    }

    // ======================== 公共入口（Block 和 Entity 共用） ========================

    /**
     * 统一的传送门交互处理入口。
     * 由 PortalTriggerBlock 和旧 Interaction 实体共同调用。
     *
     * @param player   触发交互的玩家
     * @param targetId 传送目标 ID（如 "pierre_house_enter", "farm_exit_south" 等）
     */
    public static void handlePortalInteraction(ServerPlayer player, String targetId) {
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim = player.serverLevel().dimension();
        boolean inStardew = ModDimensions.STARDEW_VALLEY.equals(dim);
        boolean inOverworld = net.minecraft.world.level.Level.OVERWORLD.equals(dim);
        boolean inMine = ModMiningDimensions.STARDEW_MINING.equals(dim);

        if (!inStardew && !inOverworld && !inMine) return;

        // ── 矿井维度：仅处理矿井出口 ──
        if (inMine) {
            if ("mine_exit".equals(targetId)) {
                handleMineExit(player);
            } else if ("skull_cavern_exit".equals(targetId)) {
                handleSkullCavernExit(player);
            }
            return;
        }

        // ── 主世界：巫师塔入口 ──
        if (inOverworld) {
            if ("wizard_tower_overworld_enter".equals(targetId)) {
                CrossDimensionTeleporter.overworldToWizardInterior(player);
            }
            return;
        }

        // ── 星露谷维度 ──

        // 矿井入口
        if ("mine_entrance".equals(targetId)) {
            handleMineEntrance(player);
            return;
        }

        // 骷髅矿入口（沙漠 → floor 121）
        if ("desert_mine_enter".equals(targetId)) {
            handleDesertMineEntrance(player);
            return;
        }

        // 沙漠公交站（买票前往沙漠）
        if ("desert_bus".equals(targetId)) {
            handleDesertBus(player);
            return;
        }

        // 沙漠公交站（返程鹈鹕镇，免费）
        if ("desert_bus_return".equals(targetId)) {
            handleDesertBusReturn(player);
            return;
        }

        // 农场入口
        if (targetId.startsWith("farm_entry_")) {
            handleFarmEntry(player, targetId);
            return;
        }

        // 农场出口
        if (targetId.startsWith("farm_exit_")) {
            handleFarmExit(player, targetId);
            return;
        }

        // 社区中心入口
        if ("community_center_enter".equals(targetId)) {
            handleCCEntry(player);
            return;
        }

        // 社区中心出口
        if ("community_center_exit".equals(targetId)) {
            handleCCExit(player);
            return;
        }

        // 温室入口
        if ("greenhouse_enter".equals(targetId)) {
            handleGreenhouseEntry(player);
            return;
        }

        // 温室出口
        if ("greenhouse_exit".equals(targetId)) {
            handleGreenhouseExit(player);
            return;
        }

        // 采石场入口（需要完成工艺室献祭）
        if ("quarry_entrance".equals(targetId)) {
            handleQuarryEntrance(player);
            return;
        }

        // 采石场出口（无条件返程）
        if ("quarry_exit".equals(targetId)) {
            handleQuarryExit(player);
            return;
        }

        // 巫师塔 → 回主世界
        if ("wizard_tower_return_overworld".equals(targetId)) {
            CrossDimensionTeleporter.wizardInteriorToOverworld(player);
            return;
        }

        // 巫师塔出口（基于任务状态路由）
        if ("wizard_tower_exit".equals(targetId)) {
            com.stardew.craft.player.PlayerStardewData pdata = com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
            if (!pdata.isWizardQuestComplete()) {
                net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> src = pdata.getWizardSourceDimension();
                if (src != null && net.minecraft.world.level.Level.OVERWORLD.equals(src)) {
                    CrossDimensionTeleporter.wizardInteriorToOverworld(player);
                    return;
                }
            }
            // 任务已完成 或 来自星露谷 → 走下面的通用 portal 逻辑
        }

        // 巫师塔入口（记录来源维度）
        if ("wizard_tower_enter".equals(targetId)) {
            com.stardew.craft.player.PlayerStardewData pdata = com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
            pdata.setWizardSourceDimension(ModDimensions.STARDEW_VALLEY);
        }

        // ── 通用 Portal Registry 查找 ──
        Optional<InteriorPortalRegistry.PortalTarget> resolved = InteriorPortalRegistry.resolve(targetId);
        if (resolved.isEmpty()) return;

        // Museum exit guard
        if ("museum_exit".equals(targetId)) {
            com.stardew.craft.museum.MuseumDonationData museumData =
                com.stardew.craft.museum.MuseumDonationData.get(player.serverLevel());
            if (museumData.isDonationModeActive(player.getUUID())) {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                    new com.stardew.craft.network.payload.OpenNpcDialogueScreenPayload(
                        "gunther",
                        "stardewcraft.npc.gunther.donation_exit_blocked",
                        0
                    ));
                return;
            }
        }

        // 确保室内布局已初始化
        InteriorSubspaceManager.ensureLoaded(player.serverLevel(), "portal_interaction");

        // 冷却检查
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        InteriorPortalRegistry.PortalTarget t = resolved.get();

        // 传送前清理
        player.closeContainer();
        player.stopUsingItem();

        player.teleportTo(
            player.serverLevel(),
            t.x(), t.y(), t.z(),
            t.yaw(), t.pitch()
        );

        applyInteriorFlag(player, t.mode());
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
    }

    // ======================== 工具方法 ========================

    private static Optional<String> findTagValue(Set<String> tags, String prefix) {
        for (String tag : tags) {
            if (tag != null && tag.startsWith(prefix) && tag.length() > prefix.length()) {
                return Optional.of(tag.substring(prefix.length()));
            }
        }
        return Optional.empty();
    }

    private static void applyInteriorFlag(ServerPlayer player, InteriorPortalRegistry.PortalMode mode) {
        if (mode == InteriorPortalRegistry.PortalMode.ENTRANCE) {
            player.getPersistentData().putBoolean(PLAYER_FLAG_INTERIOR, true);
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
            return;
        }
        if (mode == InteriorPortalRegistry.PortalMode.EXIT) {
            player.getPersistentData().putBoolean(PLAYER_FLAG_INTERIOR, false);
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    public static boolean isPlayerInInteriorSpace(ServerPlayer player) {
        return player.getPersistentData().getBoolean(PLAYER_FLAG_INTERIOR);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (player.tickCount % 40 == 0) {
                if (isPlayerInInteriorSpace(player)) {
                    if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
                    }
                }
            }
        }
    }

    // ======================== 沙漠公交站 ========================

    private static void handleDesertBus(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);

        com.stardew.craft.desert.DesertBusService.beginBusRide(player);
    }

    private static void handleDesertBusReturn(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);

        com.stardew.craft.desert.DesertBusService.beginReturnRide(player);
    }

    // ======================== 矿井跨维度传送 ========================

    private static final double MINE_OUTDOOR_X = -285.5;
    private static final double MINE_OUTDOOR_Y = -12.0;
    private static final double MINE_OUTDOOR_Z = 314.5;
    private static final double MINE_INDOOR_X = 21.5;
    private static final double MINE_INDOOR_Y = 66.0;
    private static final double MINE_INDOOR_Z = 3.5;

    private static void handleFarmEntry(ServerPlayer player, String entryTag) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);

        com.stardew.craft.farm.FarmInstanceRegistry registry = com.stardew.craft.farm.FarmInstanceRegistry.get();
        com.stardew.craft.farm.FarmInstance myFarm = registry.getFarmForPlayer(player.getUUID());

        if (myFarm == null) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                    new com.stardew.craft.network.payload.OpenFarmSelectionPayload());
            StardewCraft.LOGGER.info("[FARM_ENTRY] {} has no farm, opening selection screen",
                    player.getName().getString());
            return;
        }

        com.stardew.craft.network.payload.FarmListSyncPayload.sendToPlayer(player, entryTag);
        StardewCraft.LOGGER.info("[FARM_ENTRY] {} opening farm entry GUI via {}",
                player.getName().getString(), entryTag);
    }

    private static final net.minecraft.core.BlockPos EXIT_SOUTH_TARGET = new net.minecraft.core.BlockPos(200, -14, 162);
    private static final net.minecraft.core.BlockPos EXIT_EAST_TARGET = new net.minecraft.core.BlockPos(212, -14, 24);
    private static final net.minecraft.core.BlockPos EXIT_WEST_TARGET = new net.minecraft.core.BlockPos(68, -12, 119);

    private static void handleFarmExit(ServerPlayer player, String exitId) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);

        com.stardew.craft.farm.FarmInstance farm = com.stardew.craft.farm.FarmInstanceRegistry.get()
                .getFarmForPlayer(player.getUUID());
        if (farm != null) {
            com.stardew.craft.farm.FarmChunkManager.get().onPlayerLeaveFarm(
                    player.serverLevel(), player, farm);
        }

        net.minecraft.core.BlockPos target = switch (exitId) {
            case "farm_exit_south" -> EXIT_SOUTH_TARGET;
            case "farm_exit_east" -> EXIT_EAST_TARGET;
            case "farm_exit_west" -> EXIT_WEST_TARGET;
            default -> EXIT_SOUTH_TARGET;
        };

        player.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        StardewCraft.LOGGER.info("[FARM_EXIT] {} exited farm via {} to {}",
                player.getName().getString(), exitId, target);
    }

    private static void handleMineEntrance(ServerPlayer player) {
        ServerLevel mineLevel = player.server.getLevel(ModMiningDimensions.STARDEW_MINING);
        if (mineLevel == null) {
            StardewCraft.LOGGER.warn("[MINE_PORTAL] Mine dimension not available");
            return;
        }

        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        MineEntranceBootstrap.ensureGenerated(mineLevel);
        player.teleportTo(mineLevel, MINE_INDOOR_X, MINE_INDOOR_Y, MINE_INDOOR_Z, 0.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
    }

    /** 骷髅矿入口：传送到 floor 121 大厅 schem 内部 (origin + 3, 1, 3) */
    private static void handleDesertMineEntrance(ServerPlayer player) {
        ServerLevel mineLevel = player.server.getLevel(ModMiningDimensions.STARDEW_MINING);
        if (mineLevel == null) {
            StardewCraft.LOGGER.warn("[SKULL_CAVERN] Mine dimension not available");
            return;
        }

        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        // SDV 原版门禁：必须拥有 SkullKey 才能进入 (GameLocation.SkullDoor)
        com.stardew.craft.player.PlayerStardewData sdData =
                com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
        if (!sdData.hasMailFlag(com.stardew.craft.communitycenter.state.CCStoryFlags.HAS_SKULL_KEY)) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("message.stardewcraft.skull_door_locked"),
                false);
            player.playNotifySound(net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.6f, 0.7f);
            player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
            return;
        }

        // 生成 floor 121 入口大厅（使用 skullkeyentrance.schem）
        com.stardew.craft.mining.MineFloorGenerator.generateFloor(mineLevel, 121);

        // 标记跳过 DimensionEventHandler.onPlayerChangeDimension 的自动传送
        // 否则会被覆盖到普通矿井大厅 (21.5, 66, 3.5)
        com.stardew.craft.interior.CrossDimensionTeleporter.markSkipAutoTeleport(player.getUUID());

        // 传送到 schem 内部 spawn (origin + 3, 1, 3)
        net.minecraft.core.BlockPos spawn = com.stardew.craft.mining.MineFloorGenerator.SKULL_CAVERN_LOBBY_SPAWN;
        player.setInvulnerable(true);
        player.teleportTo(mineLevel,
                spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D,
                0.0F, 0.0F);
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0;
        player.hurtMarked = true;
        mineLevel.getServer().tell(new net.minecraft.server.TickTask(
                mineLevel.getServer().getTickCount() + 10,
                () -> {
                    if (!player.isCreative()) player.setInvulnerable(false);
                }));

        // 更新玩家矿井数据
        com.stardew.craft.mining.MiningPlayerData pData = com.stardew.craft.mining.MiningDataManager.getPlayerData(player);
        if (pData != null) {
            pData.setCurrentFloor(121);
            com.stardew.craft.mining.MiningDataManager.savePlayerData(player, pData);
        }

        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
            player,
            new com.stardew.craft.network.MiningFloorSyncPacket(121)
        );

        com.stardew.craft.mining.SkullCavernSessionManager.onPlayerEnter(player);

        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        StardewCraft.LOGGER.info("[SKULL_CAVERN] {} entered skull cavern lobby at {}", player.getName().getString(), spawn);
    }

    /** 骷髅矿大厅出口：传回沙漠 (-339, -42, 1268) 朝南 */
    private static void handleSkullCavernExit(ServerPlayer player) {
        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            StardewCraft.LOGGER.warn("[SKULL_CAVERN] Stardew Valley dimension not available");
            return;
        }

        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        player.closeContainer();
        player.stopUsingItem();

        // session 清理（若本层无其他玩家则重置）
        com.stardew.craft.mining.SkullCavernSessionManager.onPlayerLeave(player, player.serverLevel());

        com.stardew.craft.interior.CrossDimensionTeleporter.markSkipAutoTeleport(player.getUUID());

        net.minecraft.core.BlockPos arrival = com.stardew.craft.desert.DesertConstants.worldPos(
                com.stardew.craft.desert.DesertConstants.SKULL_CAVERN_EXIT_OFFSET);
        player.teleportTo(stardewLevel,
                arrival.getX() + 0.5D, arrival.getY(), arrival.getZ() + 0.5D,
                180.0F, 0.0F);

        // 重置楼层显示为 0（离开骷髅矿）
        com.stardew.craft.mining.MiningPlayerData pData = com.stardew.craft.mining.MiningDataManager.getPlayerData(player);
        if (pData != null) {
            pData.setCurrentFloor(0);
            com.stardew.craft.mining.MiningDataManager.savePlayerData(player, pData);
        }
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
            player,
            new com.stardew.craft.network.MiningFloorSyncPacket(0)
        );

        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        StardewCraft.LOGGER.info("[SKULL_CAVERN] {} exited skull cavern to desert at {}", player.getName().getString(), arrival);
    }

    private static void handleMineExit(ServerPlayer player) {
        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            StardewCraft.LOGGER.warn("[MINE_PORTAL] Stardew Valley dimension not available");
            return;
        }

        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        player.closeContainer();
        player.stopUsingItem();

        com.stardew.craft.interior.CrossDimensionTeleporter.markSkipAutoTeleport(player.getUUID());

        player.teleportTo(stardewLevel, MINE_OUTDOOR_X, MINE_OUTDOOR_Y, MINE_OUTDOOR_Z,
                          180.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
    }

    // ════════════════════════════════════════════════════════════════
    //  社区中心 / 温室 — 玩家独立室内空间
    // ════════════════════════════════════════════════════════════════

    private static void handleCCEntry(ServerPlayer player) {
        // SDV parity: CC door is locked until event 611439 (lewis_cc_tour) sets ccDoorUnlock
        // Original: if (Game1.MasterPlayer.mailReceived.Contains("ccDoorUnlock") || Game1.MasterPlayer.mailReceived.Contains("JojaMember"))
        com.stardew.craft.player.PlayerStardewData data =
                com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
        if (!data.hasMailFlag(com.stardew.craft.communitycenter.state.CCStoryFlags.CC_DOOR_UNLOCKED)
                && !data.hasMailFlag("JojaMember")) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("stardewcraft.portal.cc.locked"),
                    true);
            return;
        }
        handleCCEntryCore(player);
    }

    /**
     * Public entry for cutscene use — skips cooldown check.
     */
    public static void handleCCEntryForCutscene(ServerPlayer player) {
        handleCCEntryCore(player);
    }

    private static void handleCCEntryCore(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        InteriorSubspaceManager.ensureLoaded(level, "cc_entry");

        PlayerInteriorAllocator alloc = PlayerInteriorAllocator.get(level);
        net.minecraft.core.BlockPos ccOrigin = alloc.ensureCCLoaded(level, player.getUUID());
        net.minecraft.core.BlockPos spawnPos = ccOrigin.offset(InteriorSubspaceManager.CC_INDOOR_SPAWN_OFFSET);

        player.closeContainer();
        player.stopUsingItem();

        player.teleportTo(level,
            spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
            -90.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, player.serverLevel().getGameTime());
        applyInteriorFlag(player, InteriorPortalRegistry.PortalMode.ENTRANCE);

        StardewCraft.LOGGER.debug("[CC-PORTAL] Player {} entered their CC at {}", player.getName().getString(), ccOrigin);
    }

    private static void handleCCExit(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        net.minecraft.core.BlockPos exitPos = InteriorSubspaceManager.CC_OUTDOOR_EXIT_POS;

        player.closeContainer();
        player.stopUsingItem();

        player.teleportTo(player.serverLevel(),
            exitPos.getX() + 0.5D, exitPos.getY(), exitPos.getZ() + 0.5D,
            180.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        applyInteriorFlag(player, InteriorPortalRegistry.PortalMode.EXIT);
    }

    private static void handleGreenhouseEntry(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        ServerLevel level = player.serverLevel();
        InteriorSubspaceManager.ensureLoaded(level, "greenhouse_entry");

        PlayerInteriorAllocator alloc = PlayerInteriorAllocator.get(level);
        net.minecraft.core.BlockPos ghOrigin = alloc.ensureGreenhouseLoaded(level, player.getUUID());
        net.minecraft.core.BlockPos spawnPos = ghOrigin.offset(InteriorSubspaceManager.GREENHOUSE_INDOOR_SPAWN_OFFSET);

        player.closeContainer();
        player.stopUsingItem();

        player.teleportTo(level,
            spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
            -90.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        applyInteriorFlag(player, InteriorPortalRegistry.PortalMode.ENTRANCE);

        StardewCraft.LOGGER.debug("[GH-PORTAL] Player {} entered their greenhouse at {}", player.getName().getString(), ghOrigin);
    }

    // ════════════════════════════════════════════════════════════════
    //  采石场访问（工艺室献祭解锁）
    // ════════════════════════════════════════════════════════════════

    private static void handleQuarryEntrance(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        com.stardew.craft.player.PlayerStardewData data =
                com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
        if (!data.hasMailFlag(com.stardew.craft.communitycenter.state.CCStoryFlags.CC_CRAFTS_ROOM)) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("stardewcraft.portal.quarry.blocked"),
                    true);
            player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
            return;
        }

        player.closeContainer();
        player.stopUsingItem();
        player.teleportTo(player.serverLevel(),
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.ENTRY_DEST_X,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.ENTRY_DEST_Y,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.ENTRY_DEST_Z,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.ENTRY_DEST_YAW,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.ENTRY_DEST_PITCH);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
    }

    private static void handleQuarryExit(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        player.closeContainer();
        player.stopUsingItem();
        player.teleportTo(player.serverLevel(),
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.EXIT_DEST_X,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.EXIT_DEST_Y,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.EXIT_DEST_Z,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.EXIT_DEST_YAW,
                com.stardew.craft.communitycenter.quarry.QuarryAccessManager.EXIT_DEST_PITCH);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
    }

    private static void handleGreenhouseExit(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

        player.closeContainer();
        player.stopUsingItem();

        net.minecraft.core.BlockPos exitPos = com.stardew.craft.greenhouse.GreenhouseManager.getExitPosForPlayer(player);

        player.teleportTo(player.serverLevel(),
            exitPos.getX() + 0.5D, exitPos.getY(), exitPos.getZ() + 0.5D,
            -90.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        applyInteriorFlag(player, InteriorPortalRegistry.PortalMode.EXIT);
    }
}
