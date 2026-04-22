package com.stardew.craft.player;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.core.ModTags;
import com.stardew.craft.item.equipment.StardewBootsItem;
import com.stardew.craft.item.equipment.StardewRingItem;
import com.stardew.craft.item.weapon.StardewWeaponItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 晕倒/死亡核心逻辑。
 * <p>
 * 对标 SDV 原版 Farmer.cs / Game1.cs：
 * <ul>
 *   <li>战斗死亡（矿井 vs 非矿井）→ 金币扣除 + 物品丢失 + 次日体力压到2</li>
 *   <li>2AM 晕倒 → 金币扣除（min(1000, money/10)）</li>
 * </ul>
 * 创造模式对所有惩罚完全豁免。
 */
@SuppressWarnings("null")
public final class PassOutService {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * 防止同一 tick 内对同一玩家重复触发 onCombatDeath。
     * 因为 onPlayerHurt（HP→0）和 onPlayerDeath 可能在同一帧先后触发。
     */
    private static final java.util.Map<java.util.UUID, Long> lastKnockoutTick = new java.util.WeakHashMap<>();

    /**
     * 击倒状态：玩家 HP 归零后到传送完成前，完全免疫所有伤害。
     * 防止在持续伤害环境（如岩浆块）中反复死亡导致黑屏卡死。
     */
    private static final java.util.Set<java.util.UUID> knockedOutPlayers = java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());

    /**
     * 2AM 晕倒的惩罚结果（暂存），由 advanceDayWithSleepTime 消费后合并进 OvernightSettlementPayload。
     * 不再单独发送 PassOutPayload，避免与 OvernightSettlementPayload 客户端画面冲突。
     */
    private static final java.util.Map<java.util.UUID, PassOutResult> pendingPassOutResults = new java.util.concurrent.ConcurrentHashMap<>();

    /** 2AM 晕倒惩罚结果 */
    public record PassOutResult(PassOutType type, int moneyLost, List<net.minecraft.world.item.ItemStack> lostItems) {}

    /** 消费指定玩家的晕倒结果（一次性），返回 null 表示该玩家未晕倒 */
    @javax.annotation.Nullable
    public static PassOutResult consumePassOutResult(java.util.UUID playerId) {
        return pendingPassOutResults.remove(playerId);
    }

    /** 检查玩家是否处于击倒状态（正在黑屏过渡中） */
    public static boolean isKnockedOut(ServerPlayer player) {
        return knockedOutPlayers.contains(player.getUUID());
    }

    /** 清除击倒状态（传送完成后调用） */
    public static void clearKnockedOut(ServerPlayer player) {
        knockedOutPlayers.remove(player.getUUID());
    }

    private PassOutService() {}

    // ──────────────────────────────────────
    //  A/B: 战斗死亡（HP 归零）
    // ──────────────────────────────────────

    /**
     * 由 {@link StardewDamageHooks} 的 KnockoutHandler 调用。
     */
    public static void onCombatDeath(ServerPlayer player, DamageSource source) {
        // 防重入：同一 tick 不重复处理
        long currentTick = player.level().getGameTime();
        Long lastTick = lastKnockoutTick.get(player.getUUID());
        if (lastTick != null && lastTick == currentTick) {
            return;
        }
        lastKnockoutTick.put(player.getUUID(), currentTick);

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        // 创造模式豁免
        if (player.isCreative()) {
            data.setHealth(data.getMaxHealth());
            syncAndSave(player, data);
            return;
        }

        boolean inMine = player.level().dimension() == ModMiningDimensions.STARDEW_MINING;

        // 1. 金币惩罚
        int moneyLost = calcCombatMoneyLoss(data, player, inMine);
        if (moneyLost > 0) {
            data.removeMoney(moneyLost);
        }

        // 2. 物品丢失
        List<ItemStack> lostItems = loseItemsOnDeath(player, data);
        data.setItemsLostLastDeath(lostItems);

        // 3. 战斗死亡标志（次日体力压到2）
        data.setPassedOutFromCombat(true);

        // 4. 标记击倒状态（HP 保持 0，免疫后续伤害，直到传送完成）
        knockedOutPlayers.add(player.getUUID());
        // 不恢复 SDV 血量——保持 0 直到传送后再恢复

        // 5. 安排死亡邮件
        scheduleDeathMail(player, data, inMine, moneyLost, !lostItems.isEmpty());

        // 6. 发送客户端通知
        PacketDistributor.sendToPlayer(player,
                new com.stardew.craft.network.payload.PassOutPayload(
                        inMine ? PassOutType.COMBAT_MINE : PassOutType.COMBAT_OVERWORLD,
                        moneyLost, lostItems));

        syncAndSave(player, data);

        LOGGER.info("[PASSOUT] {} combat death in {} — lost {}g, {} items",
                player.getName().getString(),
                inMine ? "mine" : "overworld",
                moneyLost, lostItems.size());
    }

    // ──────────────────────────────────────
    //  C: 2:00 AM 晕倒
    // ──────────────────────────────────────

    /**
     * 由 {@link com.stardew.craft.event.DimensionEventHandler} 在 2AM 时调用。
     *
     * @return 扣了多少钱（用于客户端显示）
     */
    public static int on2AMPassOut(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        // 创造模式豁免
        if (player.isCreative()) {
            return 0;
        }

        // 金币惩罚
        int moneyLost = Math.min(1000, data.getMoney() / 10);
        if (moneyLost > 0) {
            data.removeMoney(moneyLost);
        }

        // 安排晕倒邮件
        schedulePassOutMail(player, data, moneyLost);

        // 不再发送单独的 PassOutPayload —— 晕倒数据将合并进 OvernightSettlementPayload，
        // 由客户端在结算流程中统一展示（先渐黑→摘要→升级→出货），避免两个画面冲突。
        pendingPassOutResults.put(player.getUUID(),
                new PassOutResult(PassOutType.EXHAUSTION_2AM, moneyLost, List.of()));

        syncAndSave(player, data);

        LOGGER.info("[PASSOUT] {} 2AM pass out — lost {}g",
                player.getName().getString(), moneyLost);

        return moneyLost;
    }

    // ──────────────────────────────────────
    //  D: 体力耗尽晕倒（energy ≤ -15）
    // ──────────────────────────────────────

    /**
     * 由 PlayerDataEventHandler.onPlayerTick() 在 energy ≤ -15 时调用。
     * 惩罚同 2AM 晕倒：金币扣除 + 邮件，无物品丢失。
     */
    public static void onExhaustionPassOut(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        if (player.isCreative()) {
            return;
        }

        // 金币惩罚（同 2AM）
        int moneyLost = Math.min(1000, data.getMoney() / 10);
        if (moneyLost > 0) {
            data.removeMoney(moneyLost);
        }

        // 安排晕倒邮件
        schedulePassOutMail(player, data, moneyLost);

        // 发送客户端通知
        PacketDistributor.sendToPlayer(player,
                new com.stardew.craft.network.payload.PassOutPayload(
                        PassOutType.EXHAUSTION_STAMINA,
                        moneyLost, List.of()));

        syncAndSave(player, data);

        LOGGER.info("[PASSOUT] {} exhaustion pass out — lost {}g",
                player.getName().getString(), moneyLost);
    }

    // ──────────────────────────────────────
    //  金币计算
    // ──────────────────────────────────────

    private static int calcCombatMoneyLoss(PlayerStardewData data, ServerPlayer player, boolean inMine) {
        int money = data.getMoney();
        if (money <= 0) return 0;

        int moneyToLose;
        if (inMine) {
            // SDV: rand(Money/40, Money/8), cap 15000
            int lo = money / 40;
            int hi = money / 8;
            if (hi <= lo) {
                moneyToLose = lo;
            } else {
                moneyToLose = lo + RANDOM.nextInt(hi - lo + 1);
            }
            moneyToLose = Math.min(moneyToLose, 15000);

            // 幸运等级减免
            int luckLevel = PlayerStardewDataAPI.getLuckLevel(player);
            moneyToLose -= (int)(luckLevel * 0.01 * moneyToLose);

            // 向下取整到百位
            moneyToLose -= moneyToLose % 100;
        } else {
            // 非矿井：固定上限 1000g
            moneyToLose = Math.min(1000, money);
        }

        return Math.max(0, moneyToLose);
    }

    // ──────────────────────────────────────
    //  物品丢失
    // ──────────────────────────────────────

    private static List<ItemStack> loseItemsOnDeath(ServerPlayer player, PlayerStardewData data) {
        List<ItemStack> lostItems = new ArrayList<>();
        int luckLevel = PlayerStardewDataAPI.getLuckLevel(player);
        double dailyLuck = data.getDailyLuck();
        double itemLossRate = 0.22 - luckLevel * 0.04 - dailyLuck;

        // 从背包末尾向前遍历（SDV 原版行为）
        var inventory = player.getInventory();
        int lost = 0;
        for (int slot = inventory.getContainerSize() - 1; slot >= 0; slot--) {
            if (lost >= 3) break;
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) continue;
            if (!canBeLostOnDeath(stack)) continue;
            if (RANDOM.nextDouble() < itemLossRate) {
                lostItems.add(stack.copy());
                inventory.setItem(slot, ItemStack.EMPTY);
                lost++;
            }
        }
        return lostItems;
    }

    /**
     * SDV 原版：工具、武器、戒指、靴子不可丢失；
     * 非星露谷物品不参与丢失；标记 prevent_loss_on_death 的物品不可丢失。
     */
    private static boolean canBeLostOnDeath(ItemStack stack) {
        var item = stack.getItem();
        // 只有 stardewcraft 命名空间的物品可以丢失
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
        if (!StardewCraft.MODID.equals(id.getNamespace())) return false;
        // 数据标签黑名单（SDV prevent_loss_on_death 等价）
        if (stack.is(ModTags.Items.PREVENT_LOSS_ON_DEATH)) return false;
        // 星露谷工具（锄头、斧头、镐、浇水壶、钓竿、镰刀、淘金盘）
        if (item instanceof net.minecraft.world.item.TieredItem) return false;
        if (item instanceof com.stardew.craft.item.tool.HoeItem) return false;
        if (item instanceof com.stardew.craft.item.tool.WateringCanItem) return false;
        if (item instanceof com.stardew.craft.item.tool.FishingRodItem) return false;
        if (item instanceof com.stardew.craft.item.tool.ScytheItem) return false;
        if (item instanceof com.stardew.craft.item.tool.PanItem) return false;
        // 武器（剑、匕首、棍棒）
        if (item instanceof StardewWeaponItem) return false;
        if (item instanceof com.stardew.craft.item.weapon.StardewDaggerItem) return false;
        if (item instanceof com.stardew.craft.item.weapon.StardewClubItem) return false;
        // 戒指
        if (item instanceof StardewRingItem) return false;
        // 靴子
        if (item instanceof StardewBootsItem) return false;
        return true;
    }

    // ──────────────────────────────────────
    //  邮件安排
    // ──────────────────────────────────────

    private static final String[] MINE_DEATH_MAILS = {
            "mineDeath_Robin", "mineDeath_Clint", "mineDeath_Maru", "mineDeath_Linus"
    };

    private static void scheduleDeathMail(ServerPlayer player, PlayerStardewData data,
                                          boolean inMine, int moneyLost, boolean itemsLost) {
        if (inMine) {
            // 随机选一个矿井救援邮件
            String mailId = MINE_DEATH_MAILS[RANDOM.nextInt(MINE_DEATH_MAILS.length)];
            data.addMailForTomorrow(mailId);
        } else {
            // Harvey 诊所邮件
            data.addMailForTomorrow("hospitalDeath");
        }

        // Marlon 物品找回邮件
        if (itemsLost) {
            data.addMailForTomorrow("marlonRecovery");
        }
    }

    private static void schedulePassOutMail(ServerPlayer player, PlayerStardewData data, int moneyLost) {
        // 简化版：Linus 40%, Harvey 40%, Marlon 20%
        double roll = RANDOM.nextDouble();
        String mailId;
        if (roll < 0.4) {
            mailId = "passedOut_Linus";
        } else if (roll < 0.8) {
            mailId = "passedOut_Harvey";
        } else {
            mailId = "passedOut_Marlon";
        }
        data.addMailForTomorrow(mailId);
    }

    // ──────────────────────────────────────
    //  次日体力覆盖（由 StardewTimeManager 调用）
    // ──────────────────────────────────────

    /**
     * 在 sleep() 恢复能量之后调用，若有战斗死亡标志则压到 2。
     */
    public static void applyCombatDeathEnergyPenalty(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.isPassedOutFromCombat()) {
            data.setEnergy(Math.min(data.getEnergy(), 2.0f));
            data.setPassedOutFromCombat(false);
        }
    }

    // ──────────────────────────────────────
    //  传送回农场
    // ──────────────────────────────────────

    /**
     * 将玩家传送回星露谷维度农场出生点。
     */
    public static void teleportToFarmSpawn(ServerPlayer player) {
        var server = player.server;
        var stardewLevel = server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) return;

        // 查询玩家的农场出生点
        com.stardew.craft.farm.FarmInstanceRegistry registry = com.stardew.craft.farm.FarmInstanceRegistry.get();
        net.minecraft.core.BlockPos spawnPos = registry.getFarmSpawnPoint(player.getUUID());
        double sx = spawnPos != null ? spawnPos.getX() + 0.5 : 150.5;
        double sy = spawnPos != null ? spawnPos.getY() : -12;
        double sz = spawnPos != null ? spawnPos.getZ() + 0.5 : 119.5;

        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY) {
            player.teleportTo(stardewLevel, sx, sy, sz, player.getYRot(), player.getXRot());
        } else {
            player.teleportTo(sx, sy, sz);
        }

        // 传送完成：清除击倒状态，恢复满血
        clearKnockedOut(player);
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        data.setHealth(data.getMaxHealth());
        PlayerDataEventHandler.syncPlayerData(player, data);
        PlayerDataManager.get().setDirty();

        // 重置矿井层数到 0（SDV 原版：晕倒后回到大厅）
        com.stardew.craft.mining.MiningPlayerData miningData =
            com.stardew.craft.mining.MiningDataManager.getPlayerData(player);
        miningData.setCurrentFloor(0);
        com.stardew.craft.mining.MiningDataManager.savePlayerData(player, miningData);
    }

    // ──────────────────────────────────────
    //  工具方法
    // ──────────────────────────────────────

    private static void syncAndSave(ServerPlayer player, PlayerStardewData data) {
        PlayerDataEventHandler.syncPlayerData(player, data);
        PlayerDataManager.get().setDirty();
    }

    /**
     * 晕倒类型枚举。
     */
    public enum PassOutType {
        COMBAT_MINE(0),
        COMBAT_OVERWORLD(1),
        EXHAUSTION_2AM(2),
        EXHAUSTION_STAMINA(3);

        private final int id;
        PassOutType(int id) { this.id = id; }
        public int getId() { return id; }

        public static PassOutType fromId(int id) {
            return switch (id) {
                case 0 -> COMBAT_MINE;
                case 1 -> COMBAT_OVERWORLD;
                case 2 -> EXHAUSTION_2AM;
                case 3 -> EXHAUSTION_STAMINA;
                default -> EXHAUSTION_2AM;
            };
        }
    }
}
