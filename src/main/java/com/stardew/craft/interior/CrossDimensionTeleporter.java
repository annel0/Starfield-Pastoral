package com.stardew.craft.interior;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.blockentity.WoodenChestBlockEntity;
import com.stardew.craft.network.payload.StarterChestHintPayload;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 跨维度传送工具：处理 Overworld ↔ Stardew Valley 巫师塔枢纽传送。
 */
@SuppressWarnings({"null", "unused"})
public final class CrossDimensionTeleporter {

    private static final String PLAYER_FLAG_INTERIOR = "stardewcraft_interior_space";
    private static final String PLAYER_LAST_PORTAL_TICK = "stardewcraft_last_portal_tick";
    private static final long PORTAL_COOLDOWN_TICKS = 8L;

    /**
     * 跳过 DimensionEventHandler 自动传送的玩家集合。
     * 当 CrossDimensionTeleporter 主动传送玩家到星露谷维度时，
     * DimensionEventHandler 不应再覆盖传送目标。
     */
    private static final Set<UUID> SKIP_AUTO_TELEPORT = ConcurrentHashMap.newKeySet();

    // 巫师塔内部亚空间坐标（与 InteriorSubspaceManager 一致）
    private static final BlockPos WIZARD_TOWER_ORIGIN = new BlockPos(18240, 70, 17088);
    private static final BlockPos WIZARD_TOWER_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 9);

    // 星露谷维度的世界原点（农场出生点）
    private static final BlockPos STARDEW_WORLD_ORIGIN = new BlockPos(150, -12, 119);

    // 星露谷维度的巫师塔室外坐标
    private static final BlockPos STARDEW_WIZARD_OUTDOOR_POS = new BlockPos(340, 0, -43);

    private CrossDimensionTeleporter() {}

    /**
     * 供 DimensionEventHandler 调用：如果该玩家正在被 CrossDimensionTeleporter
     * 传送，则消耗标记并返回 true，表示不应覆盖传送目标。
     */
    public static boolean consumeSkipAutoTeleport(UUID uuid) {
        return SKIP_AUTO_TELEPORT.remove(uuid);
    }

    /** 外部调用：标记该玩家下次进入星露谷维度时跳过自动传送 */
    public static void markSkipAutoTeleport(UUID uuid) {
        SKIP_AUTO_TELEPORT.add(uuid);
    }

    /**
     * 记录玩家在主世界的位置，供返回时使用。
     */
    public static void overworldToWizardInterior(ServerPlayer player) {
        if (checkCooldown(player)) return;

        // 记录来源信息
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        data.setOverworldReturnPos(player.blockPosition());
        data.setWizardSourceDimension(player.level().dimension());

        // 获取星露谷维度
        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            StardewCraft.LOGGER.error("[WIZARD] Stardew Valley dimension not found!");
            return;
        }

        // 确保巫师塔内部已加载
        InteriorSubspaceManager.ensureLoaded(stardewLevel, "wizard_overworld_portal");

        BlockPos spawnAbs = WIZARD_TOWER_ORIGIN.offset(WIZARD_TOWER_INDOOR_SPAWN_OFFSET);

        // 标记跳过 DimensionEventHandler 的自动传送
        SKIP_AUTO_TELEPORT.add(player.getUUID());

        // 传送前清理
        player.closeContainer();
        player.stopUsingItem();

        // 跨维度传送到巫师塔内部
        player.teleportTo(stardewLevel,
            spawnAbs.getX() + 0.5, spawnAbs.getY(), spawnAbs.getZ() + 0.5,
            180.0F, 0.0F);

        // 设置室内标志 + 夜视
        applyInteriorEnter(player);
        markCooldown(player);

        // 强制触发 NPC 系统初始化，确保巫师立刻出现
        com.stardew.craft.npc.NpcSystem.forceTickNow(stardewLevel);

        StardewCraft.LOGGER.info("[WIZARD] {} teleported from overworld to wizard tower interior", player.getName().getString());
    }

    /**
     * 从巫师塔内部回到主世界。
     * 读取之前记录的主世界坐标。
     */
    public static void wizardInteriorToOverworld(ServerPlayer player) {
        if (checkCooldown(player)) return;

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        BlockPos returnPos = data.getOverworldReturnPos();

        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            StardewCraft.LOGGER.error("[WIZARD] Overworld dimension not found!");
            return;
        }

        if (returnPos == null) {
            // Fallback: 使用世界出生点
            returnPos = overworld.getSharedSpawnPos();
            StardewCraft.LOGGER.warn("[WIZARD] No overworld return pos recorded, using spawn point");
        }

        // 传送前清理
        player.closeContainer();
        player.stopUsingItem();

        player.teleportTo(overworld,
            returnPos.getX() + 0.5, returnPos.getY(), returnPos.getZ() + 0.5,
            player.getYRot(), player.getXRot());

        // 清除室内标志
        applyInteriorExit(player);
        markCooldown(player);

        // 清理来源数据
        data.setWizardSourceDimension(null);

        StardewCraft.LOGGER.info("[WIZARD] {} teleported from wizard tower interior back to overworld at {}", player.getName().getString(), returnPos);
    }

    /**
     * 从巫师塔内部前往星露谷室外。
     * 如果玩家已有农场实例，传送到自己的农场出生点；否则传送到公共出生点。
     */
    public static void wizardInteriorToStardewOutdoor(ServerPlayer player) {
        if (checkCooldown(player)) return;

        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            StardewCraft.LOGGER.error("[WIZARD] Stardew Valley dimension not found!");
            return;
        }

        // 传送前清理
        player.closeContainer();
        player.stopUsingItem();

        // 查询玩家的农场出生点
        BlockPos spawnTarget = STARDEW_WORLD_ORIGIN;
        com.stardew.craft.farm.FarmInstanceRegistry registry = com.stardew.craft.farm.FarmInstanceRegistry.get();
        com.stardew.craft.farm.FarmInstance farm = registry.getFarm(player.getUUID());
        if (farm != null) {
            spawnTarget = farm.getSpawnPoint();
            StardewCraft.LOGGER.info("[WIZARD] {} teleporting to personal farm spawn at {}",
                    player.getName().getString(), spawnTarget);
        }

        player.teleportTo(stardewLevel,
            spawnTarget.getX() + 0.5,
            spawnTarget.getY(),
            spawnTarget.getZ() + 0.5,
            180.0F, 0.0F);

        applyInteriorExit(player);
        markCooldown(player);

        // 首次传送到星露谷：给予新手工具六件套
        giveStarterToolsIfNeeded(player);

        StardewCraft.LOGGER.info("[WIZARD] {} teleported from wizard tower interior to Stardew Valley world origin", player.getName().getString());
    }

    /**
     * 智能出口：根据玩家来源维度决定传送目标。
     * - 如果来自主世界 → 回到主世界记录坐标
     * - 如果来自星露谷（或来源未知）→ 回到星露谷室外
     */
    public static void wizardInteriorSmartExit(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        ResourceKey<Level> sourceDim = data.getWizardSourceDimension();

        if (sourceDim != null && Level.OVERWORLD.equals(sourceDim)) {
            wizardInteriorToOverworld(player);
        } else {
            // 默认行为：回到星露谷室外（与现有 wizard_tower_exit 一致）
            // 不做跨维度传送，走原有 portal 系统处理
            // 这里不需要额外处理，因为原有 exit portal 仍然存在
        }
    }

    // ──── Internal helpers ────

    /**
     * 首次到达星露谷时在玩家面前放置一个装有初始物资的木箱。
     * 箱子面朝玩家，并发送 hint 提示"点击领取初始物资"。
     */
    private static void giveStarterToolsIfNeeded(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.isStarterToolsGiven()) return;

        ServerLevel level = player.serverLevel();

        // 玩家传送时面朝南（yaw=180），面前一格 = +Z
        BlockPos chestPos = player.blockPosition().relative(Direction.SOUTH);

        // 放置木箱，面朝玩家（即面朝北）
        BlockState chestState = ModBlocks.WOODEN_CHEST.get().defaultBlockState()
                .setValue(com.stardew.craft.block.utility.WoodenChestBlock.FACING, Direction.NORTH);
        level.setBlock(chestPos, chestState, 3);

        // 填充初始物资
        BlockEntity be = level.getBlockEntity(chestPos);
        if (be instanceof WoodenChestBlockEntity chest) {
            ItemStack[] starterTools = {
                new ItemStack(ModItems.PICKAXE.get()),
                new ItemStack(ModItems.AXE.get()),
                new ItemStack(ModItems.HOE.get()),
                new ItemStack(ModItems.WATERING_CAN.get()),
                new ItemStack(ModItems.SCYTHE.get()),
                new ItemStack(ModItems.FISHING_ROD.get()),
                new ItemStack(ModItems.RUSTY_SWORD.get()),
                new ItemStack(ModItems.PARSNIP_SEEDS.get(), 15),
                new ItemStack(ModItems.MAILBOX.get()),
                new ItemStack(ModItems.SHIPPING_BIN.get()),
                new ItemStack(ModItems.BED_1.get()),
            };
            for (int i = 0; i < starterTools.length && i < chest.getContainerSize(); i++) {
                chest.setItem(i, starterTools[i]);
            }
            // 冬天到达的新玩家：额外赠送温室符文
            boolean isWinter = com.stardew.craft.time.StardewTimeManager.get().getCurrentSeason() == 3;
            if (isWinter) {
                for (int i = 0; i < chest.getContainerSize(); i++) {
                    if (chest.getItem(i).isEmpty()) {
                        chest.setItem(i, new ItemStack(ModItems.JUNIMO_GREENHOUSE_RUNE.get()));
                        break;
                    }
                }
            }
            chest.setChanged();
        }

        // 发送 hint 到客户端
        PacketDistributor.sendToPlayer(player, new StarterChestHintPayload(chestPos, true));

        data.setStarterToolsGiven(true);
        StardewCraft.LOGGER.info("[WIZARD] Placed starter chest for {} at {}", player.getName().getString(), chestPos);

        // 延迟 1 秒发送欢迎公告（让玩家先加载完场景）
        player.server.execute(() ->
            player.server.execute(() -> {
                sendWelcomeAnnouncement(player);
                if (com.stardew.craft.time.StardewTimeManager.get().getCurrentSeason() == 3) {
                    sendRuneAnnouncement(player);
                }
            })
        );
    }

    /**
     * 首次进入星露谷时发送内测欢迎公告。
     */
    private static void sendWelcomeAnnouncement(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§6§l═══════════ §e§lStardewCraft §6§l═══════════"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§c§l⚠ §f本模组尚在内测，有很多未完成的地方，不代表最终品质。"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§b§l✉ §f如果你遇到 Bug 或想提建议，欢迎私信作者 B 站账号反馈！"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§a§l📖 §f模组目前没有教程，主界面按 §e§lV §f键（可在按键设置中更改）打开。"));
        player.sendSystemMessage(Component.literal("§7   可以根据游玩星露谷物语的直觉来游玩本模组。"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§6§l═══════════════════════════════════"));
        player.sendSystemMessage(Component.literal(""));
    }

    /**
     * 冬季新玩家到达时的温室符文公告。
     */
    private static void sendRuneAnnouncement(ServerPlayer player) {
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§a§l🌿 §a═══════ §e§l祝尼魔的馈赠 §a═══════"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§f  你感受到了一股温暖而神秘的魔力……"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§f  森林深处的§a祝尼魔§f们感应到了你在寒冬中的到来。"));
        player.sendSystemMessage(Component.literal("§f  它们不忍看你的第一个冬天颗粒无收，"));
        player.sendSystemMessage(Component.literal("§f  悄悄在你的木箱中放入了一枚§e温室符文§f。"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§7  将符文放置在农田中央，7×7 范围内的作物"));
        player.sendSystemMessage(Component.literal("§7  将不受季节限制，安然生长整个冬天。"));
        player.sendSystemMessage(Component.literal("§7  符文的魔力会在下个季节来临时自然消散。"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§a  ——愿星之果实庇佑你度过这个隆冬 §e❄"));
        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§a═══════════════════════════════════"));
        player.sendSystemMessage(Component.literal(""));
    }

    private static boolean checkCooldown(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        return now - last < PORTAL_COOLDOWN_TICKS;
    }

    private static void markCooldown(ServerPlayer player) {
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, player.serverLevel().getGameTime());
    }

    private static void applyInteriorEnter(ServerPlayer player) {
        player.getPersistentData().putBoolean(PLAYER_FLAG_INTERIOR, true);
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
    }

    private static void applyInteriorExit(ServerPlayer player) {
        player.getPersistentData().putBoolean(PLAYER_FLAG_INTERIOR, false);
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
