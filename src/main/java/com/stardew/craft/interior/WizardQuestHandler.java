package com.stardew.craft.interior;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.network.payload.OpenNpcDialogueScreenPayload;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 巫师塔枢纽任务逻辑：
 * - 首次见面剧情对话
 * - 末影之眼交付检测
 * - 解锁星露谷入口
 * - 日常传送 query 分支路由
 */
@SuppressWarnings({"null", "unused"})
@EventBusSubscriber(modid = StardewCraft.MODID)
public final class WizardQuestHandler {

    private static final String NPC_ID = "wizard";

    // 巫师在巫师塔内部的位置（与 default_spawns.json 一致）
    private static final BlockPos WIZARD_POS = new BlockPos(18249, 71, 17095);
    private static final double PROXIMITY_RANGE_SQ = 5.0 * 5.0; // 5 格内自动触发
    private static final int PROXIMITY_CHECK_INTERVAL = 20; // 每秒检测一次
    private static final String PROXIMITY_COOLDOWN_TAG = "stardewcraft_wizard_prox_tick";

    // 特殊 nextDialogueNode 值，用于识别巫师传送指令
    public static final String NODE_GO_STARDEW = "wizard_go_stardew";
    public static final String NODE_GO_OVERWORLD = "wizard_go_overworld";
    public static final String NODE_STAY = "wizard_stay";
    public static final String NODE_ACCEPTED = "wizard_accepted";
    public static final String NODE_FIRST_TELEPORT = "wizard_first_teleport";
    public static final String NODE_GALAXY_CONFIRM = "wizard_galaxy_confirm";
    public static final String NODE_GALAXY_DECLINE = "wizard_galaxy_decline";

    private WizardQuestHandler() {}

    /**
     * 玩家靠近巫师时自动触发对话。
     * 仅在首次见面时自动触发（任务发布），其他情况需要右键交互。
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % PROXIMITY_CHECK_INTERVAL != 0) return;
        if (!ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())) return;

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        // 只有首次见面才自动触发对话
        if (data.isWizardFirstMet()) return;

        // 检测是否在巫师附近
        double distSq = player.blockPosition().distSqr(WIZARD_POS);
        if (distSq > PROXIMITY_RANGE_SQ) {
            player.getPersistentData().remove(PROXIMITY_COOLDOWN_TAG);
            return;
        }

        // 冷却检测：进入范围后只触发一次
        if (player.getPersistentData().contains(PROXIMITY_COOLDOWN_TAG)) return;
        player.getPersistentData().putBoolean(PROXIMITY_COOLDOWN_TAG, true);

        // 首次见面剧情对话
        data.setWizardFirstMet(true);
        sendDialogue(player, "stardewcraft.npc.wizard.intro_1", 0);
    }

    /**
     * 在 NPC 交互时调用。如果是巫师，优先处理任务相关对话。
     * @return true 如果已处理（调用方应跳过普通对话），false 走普通对话
     */
    public static boolean handleWizardInteraction(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);

        if (data.isWizardQuestComplete()) {
            // Galaxy Sword: 手持棱彩碎片 + 尚未获取过 Galaxy Sword
            if (!data.hasMailFlag("galaxySword") && findPrismaticShard(player) != null) {
                sendDialogue(player, "stardewcraft.npc.wizard.galaxy_offer", 0);
                return true;
            }
            // 任务已完成 → 走普通 NPC 对话系统（每日1次限制、对话轮转、好感度）
            return false;
        }

        if (!data.isWizardFirstMet()) {
            // 首次见面：触发剧情第1页
            data.setWizardFirstMet(true);
            sendDialogue(player, "stardewcraft.npc.wizard.intro_1", 0);
            return true;
        }

        // 已见过但未完成任务：检查背包是否有末影之眼
        ItemStack eyeSlot = findEyeOfEnder(player);
        if (eyeSlot != null) {
            eyeSlot.shrink(1);
            data.setWizardQuestComplete(true);
            sendDialogue(player, "stardewcraft.npc.wizard.has_eye", 0);
            StardewCraft.LOGGER.info("[WIZARD] {} completed wizard quest (Eye of Ender)", player.getName().getString());

            // 法师任务完成 → 立即触发故事任务（不必等到下一次 onDayStarted）
            com.stardew.craft.time.StardewTimeManager tm = com.stardew.craft.time.StardewTimeManager.get();
            int absDay = (tm.getCurrentYear() - 1) * 112 + tm.getCurrentSeason() * 28 + tm.getCurrentDay();
            data.getQuestManager().triggerStoryQuests(player, absDay);
            return true;
        }

        // 没有末影之眼：提醒（任务进行中，仍拦截）
        sendDialogue(player, "stardewcraft.npc.wizard.daily_locked", 0);
        return true;
    }

    /**
     * 处理 AnswerNpcQuestionPayload 的回调。
     * @return true 如果已处理，false 走普通 handleClientQuestionAnswer 链
     */
    public static boolean handleWizardQuestionAnswer(ServerPlayer player, String nextDialogueNode) {
        if (nextDialogueNode == null) return false;

        return switch (nextDialogueNode) {
            case NODE_GO_STARDEW -> {
                CrossDimensionTeleporter.wizardInteriorToStardewOutdoor(player);
                yield true;
            }
            case NODE_GO_OVERWORLD -> {
                CrossDimensionTeleporter.wizardInteriorToOverworld(player);
                yield true;
            }
            case NODE_STAY -> {
                // 关闭对话，什么也不做
                yield true;
            }
            case NODE_ACCEPTED -> {
                // 玩家接受了任务，显示后续对话
                sendDialogue(player, "stardewcraft.npc.wizard.accepted", 0);
                yield true;
            }
            case NODE_FIRST_TELEPORT -> {
                // 首次解锁后传送到星露谷室外（农场门前）
                CrossDimensionTeleporter.wizardInteriorToStardewOutdoor(player);
                yield true;
            }
            case NODE_GALAXY_CONFIRM -> {
                // 玩家同意交出棱彩碎片 → 获得 Galaxy Sword
                handleGalaxyConfirm(player);
                yield true;
            }
            case NODE_GALAXY_DECLINE -> {
                // 玩家拒绝，关闭对话
                yield true;
            }
            default -> false;
        };
    }

    /**
     * 玩家确认交出棱彩碎片 → 消耗碎片 → 给予 Galaxy Sword → 设置 galaxySword 标记。
     */
    private static void handleGalaxyConfirm(ServerPlayer player) {
        ItemStack shard = findPrismaticShard(player);
        if (shard == null) {
            // 碎片已不在背包（可能被丢弃），回退到日常对话
            sendDialogue(player, "stardewcraft.npc.wizard.daily_unlocked", 0);
            return;
        }
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.hasMailFlag("galaxySword")) {
            sendDialogue(player, "stardewcraft.npc.wizard.daily_unlocked", 0);
            return;
        }

        // 消耗 1 个棱彩碎片
        shard.shrink(1);

        // 给予 Galaxy Sword
        ItemStack galaxySword = new ItemStack(ModItems.GALAXY_SWORD.get());
        if (!player.getInventory().add(galaxySword)) {
            // 背包满则掉落在脚下
            player.drop(galaxySword, false);
        }

        // 设置标记 → 马龙商店解锁 Galaxy Dagger / Galaxy Hammer
        data.addMailFlag("galaxySword");

        StardewCraft.LOGGER.info("[WIZARD] {} obtained Galaxy Sword via Prismatic Shard", player.getName().getString());
        sendDialogue(player, "stardewcraft.npc.wizard.galaxy_granted", 0);
    }

    private static ItemStack findPrismaticShard(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(ModItems.PRISMATIC_SHARD.get())) {
                return stack;
            }
        }
        return null;
    }

    private static ItemStack findEyeOfEnder(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.ENDER_EYE) && !stack.isEmpty()) {
                return stack;
            }
        }
        return null;
    }

    private static void sendDialogue(ServerPlayer player, String translateKey, int friendshipPoints) {
        PacketDistributor.sendToPlayer(player,
            new OpenNpcDialogueScreenPayload(NPC_ID, translateKey, friendshipPoints));
    }
}
