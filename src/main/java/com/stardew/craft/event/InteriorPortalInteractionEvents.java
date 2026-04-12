package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.interior.CrossDimensionTeleporter;
import com.stardew.craft.interior.InteriorPortalRegistry;
import com.stardew.craft.interior.InteriorSubspaceManager;
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

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class InteriorPortalInteractionEvents {

    private static final String TAG_TARGET_PREFIX = "sdv_portal_target:";
    private static final String TAG_TO_PREFIX = "sdv_portal_to:";
    private static final String TAG_ROT_PREFIX = "sdv_portal_rot:";
    private static final String TAG_MODE_PREFIX = "sdv_portal_mode:";

    private static final String PLAYER_FLAG_INTERIOR = "stardewcraft_interior_space";
    private static final String PLAYER_LAST_PORTAL_TICK = "stardewcraft_last_portal_tick";

    private static final long PORTAL_COOLDOWN_TICKS = 8L;

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dim = player.serverLevel().dimension();
        boolean inStardew = ModDimensions.STARDEW_VALLEY.equals(dim);
        boolean inOverworld = net.minecraft.world.level.Level.OVERWORLD.equals(dim);
        boolean inMine = ModMiningDimensions.STARDEW_MINING.equals(dim);

        if (!inStardew && !inOverworld && !inMine) {
            return;
        }

        Entity target = event.getTarget();

        // ---- 矿井维度：仅处理矿井出口交互实体 ----
        if (inMine) {
            Optional<String> targetId = findTagValue(target.getTags(), TAG_TARGET_PREFIX);
            if (targetId.isPresent() && "mine_exit".equals(targetId.get())) {
                handleMineExit(player);
                event.setCanceled(true);
            }
            return;
        }

        // 主世界：仅处理巫师塔入口的跨维度传送
        if (inOverworld) {
            Optional<String> targetId = findTagValue(target.getTags(), TAG_TARGET_PREFIX);
            if (targetId.isPresent() && "wizard_tower_overworld_enter".equals(targetId.get())) {
                CrossDimensionTeleporter.overworldToWizardInterior(player);
                event.setCanceled(true);
            }
            return;
        }

        // 以下为星露谷维度内的传送逻辑

        // ---- 矿井入口：跨维度传送到矿井 ----
        Optional<String> portalTargetIdPre = findTagValue(target.getTags(), TAG_TARGET_PREFIX);
        if (portalTargetIdPre.isPresent() && "mine_entrance".equals(portalTargetIdPre.get())) {
            handleMineEntrance(player);
            event.setCanceled(true);
            return;
        }

        // 巫师塔内部"回到主世界"交互实体 → 跨维度传送
        if (portalTargetIdPre.isPresent() && "wizard_tower_return_overworld".equals(portalTargetIdPre.get())) {
            CrossDimensionTeleporter.wizardInteriorToOverworld(player);
            event.setCanceled(true);
            return;
        }

        // 巫师塔室内出口：
        // 任务完成后 → 总是回到星露谷室外（走原有 portal 系统）
        // 任务未完成、且玩家从主世界进来 → 回到主世界
        if (portalTargetIdPre.isPresent() && "wizard_tower_exit".equals(portalTargetIdPre.get())) {
            com.stardew.craft.player.PlayerStardewData pdata = com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
            if (!pdata.isWizardQuestComplete()) {
                net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> src = pdata.getWizardSourceDimension();
                if (src != null && net.minecraft.world.level.Level.OVERWORLD.equals(src)) {
                    // 任务未完成 + 玩家从主世界进来 → 回到主世界
                    CrossDimensionTeleporter.wizardInteriorToOverworld(player);
                    event.setCanceled(true);
                    return;
                }
            }
            // 任务已完成 或 来自星露谷 → 走原有 portal 系统传送到星露谷室外
        }

        // 从星露谷室外进入巫师塔：记录来源维度
        if (portalTargetIdPre.isPresent() && "wizard_tower_enter".equals(portalTargetIdPre.get())) {
            com.stardew.craft.player.PlayerStardewData pdata = com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
            pdata.setWizardSourceDimension(ModDimensions.STARDEW_VALLEY);
        }

        Optional<PortalTargetSpec> spec = resolveTargetSpec(target.getTags());
        if (spec.isEmpty()) {
            return;
        }

        // Museum exit guard: block exit if donation mode is active
        Optional<String> portalTargetId = findTagValue(target.getTags(), TAG_TARGET_PREFIX);
        if (portalTargetId.isPresent() && "museum_exit".equals(portalTargetId.get())) {
            com.stardew.craft.museum.MuseumDonationData museumData =
                com.stardew.craft.museum.MuseumDonationData.get(player.serverLevel());
            if (museumData.isDonationModeActive()) {
                // Warn the player that donation is still in progress
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                    new com.stardew.craft.network.payload.OpenNpcDialogueScreenPayload(
                        "gunther",
                        "stardewcraft.npc.gunther.donation_exit_blocked",
                        0
                    ));
                event.setCanceled(true);
                return;
            }
        }

        // 对齐矿井大厅策略：交互传送前先确保室内布局已初始化。
        InteriorSubspaceManager.ensureLoaded(player.serverLevel(), "portal_interaction");

        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) {
            event.setCanceled(true);
            return;
        }

        PortalTargetSpec targetSpec = spec.get();

        // 传送前清理：关闭容器菜单、停止使用物品，防止状态卡死
        player.closeContainer();
        player.stopUsingItem();

        player.teleportTo(
            player.serverLevel(),
            targetSpec.x,
            targetSpec.y,
            targetSpec.z,
            targetSpec.yaw,
            targetSpec.pitch
        );

        applyInteriorFlag(player, targetSpec.mode);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        event.setCanceled(true);
    }

    private static Optional<PortalTargetSpec> resolveTargetSpec(Set<String> tags) {
        Optional<String> targetId = findTagValue(tags, TAG_TARGET_PREFIX);
        if (targetId.isPresent()) {
            Optional<InteriorPortalRegistry.PortalTarget> resolved = InteriorPortalRegistry.resolve(targetId.get());
            if (resolved.isPresent()) {
                InteriorPortalRegistry.PortalTarget t = resolved.get();
                return Optional.of(new PortalTargetSpec(t.x(), t.y(), t.z(), t.yaw(), t.pitch(), t.mode()));
            }
            return Optional.empty();
        }

        Optional<String> toValue = findTagValue(tags, TAG_TO_PREFIX);
        if (toValue.isEmpty()) {
            return Optional.empty();
        }

        double[] xyz = parseDoubles(toValue.get(), 3);
        if (xyz == null) {
            return Optional.empty();
        }

        float yaw = 0.0F;
        float pitch = 0.0F;
        Optional<String> rotValue = findTagValue(tags, TAG_ROT_PREFIX);
        if (rotValue.isPresent()) {
            double[] rot = parseDoubles(rotValue.get(), 2);
            if (rot != null) {
                yaw = (float) rot[0];
                pitch = (float) rot[1];
            }
        }

        InteriorPortalRegistry.PortalMode mode = findTagValue(tags, TAG_MODE_PREFIX)
            .map(InteriorPortalInteractionEvents::parseMode)
            .orElse(InteriorPortalRegistry.PortalMode.NONE);

        return Optional.of(new PortalTargetSpec(
            xyz[0] + 0.5D,
            xyz[1],
            xyz[2] + 0.5D,
            yaw,
            pitch,
            mode
        ));
    }

    private static Optional<String> findTagValue(Set<String> tags, String prefix) {
        for (String tag : tags) {
            if (tag != null && tag.startsWith(prefix) && tag.length() > prefix.length()) {
                return Optional.of(tag.substring(prefix.length()));
            }
        }
        return Optional.empty();
    }

    private static double[] parseDoubles(String value, int expectedParts) {
        String[] parts = value.split(",");
        if (parts.length != expectedParts) {
            return null;
        }
        double[] parsed = new double[expectedParts];
        try {
            for (int i = 0; i < expectedParts; i++) {
                parsed[i] = Double.parseDouble(parts[i].trim());
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static InteriorPortalRegistry.PortalMode parseMode(String raw) {
        if (raw == null) {
            return InteriorPortalRegistry.PortalMode.NONE;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "entrance", "enter", "in" -> InteriorPortalRegistry.PortalMode.ENTRANCE;
            case "exit", "out" -> InteriorPortalRegistry.PortalMode.EXIT;
            default -> InteriorPortalRegistry.PortalMode.NONE;
        };
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

    private record PortalTargetSpec(
        double x,
        double y,
        double z,
        float yaw,
        float pitch,
        InteriorPortalRegistry.PortalMode mode
    ) {}

    // ======================== 矿井跨维度传送 ========================

    /** 矿井入口室外坐标（用于从矿井返回） */
    private static final double MINE_OUTDOOR_X = -285.5;
    private static final double MINE_OUTDOOR_Y = -12.0;
    private static final double MINE_OUTDOOR_Z = 314.5;

    /** 矿井内部入口大厅坐标 */
    private static final double MINE_INDOOR_X = 21.5;
    private static final double MINE_INDOOR_Y = 66.0;
    private static final double MINE_INDOOR_Z = 3.5;

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

        // 传送前清理
        player.closeContainer();
        player.stopUsingItem();

        // 标记跳过 DimensionEventHandler 的自动传送，防止二次传送到农场出生点
        com.stardew.craft.interior.CrossDimensionTeleporter.markSkipAutoTeleport(player.getUUID());

        player.teleportTo(stardewLevel, MINE_OUTDOOR_X, MINE_OUTDOOR_Y, MINE_OUTDOOR_Z,
                          180.0F, 0.0F);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
    }
}
