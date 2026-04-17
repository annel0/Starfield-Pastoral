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
            if (museumData.isDonationModeActive()) {
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
        com.stardew.craft.farm.FarmInstance myFarm = registry.getFarm(player.getUUID());

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
                .getFarm(player.getUUID());
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
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) return;

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
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
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
