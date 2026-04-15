package com.stardew.craft.mail;

import com.stardew.craft.network.payload.OpenMailPayload;
import com.stardew.craft.player.PlayerDataEventHandler;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件系统服务层（服务端）。
 * <p>
 * 对齐 SDV 的邮件 API：
 * - {@link #addMail(ServerPlayer, String)} — 立即投递到 mailbox（SDV Game1.addMail）
 * - {@link #addMailForTomorrow(ServerPlayer, String)} — 次日投递（SDV Game1.addMailForTomorrow）
 * - {@link #hasOrWillReceiveMail(ServerPlayer, String)} — 是否已收过/将收到
 * - {@link #openNextMail(ServerPlayer)} — 从 mailbox 弹出并发送到客户端
 */
@SuppressWarnings("null")
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    /**
     * 立即投递邮件到玩家信箱。如果玩家已收过（mailFlags）或信箱中已有，则忽略。
     */
    public static void addMail(ServerPlayer player, String mailId) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.hasMailFlag(mailId)) return;  // 已读过
        data.addToMailbox(mailId);
    }

    /**
     * 安排邮件于次日投递。如果已收过或已安排，则忽略。
     */
    public static void addMailForTomorrow(ServerPlayer player, String mailId) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.hasMailFlag(mailId)) return;
        data.addMailForTomorrow(mailId);
    }

    /**
     * 检查玩家是否已经收过、正在信箱中、或已安排明天投递此邮件。
     */
    public static boolean hasOrWillReceiveMail(ServerPlayer player, String mailId) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        return data.hasMailFlag(mailId)
                || data.getMailbox().contains(mailId)
                || data.getMailForTomorrow().contains(mailId);
    }

    /**
     * 从信箱弹出下一封邮件并发送到客户端显示。
     * 如果信箱为空，发送聊天消息提示。
     */
    public static void openNextMail(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (!data.hasMailInMailbox()) {
            player.sendSystemMessage(Component.translatable("stardewcraft.mailbox.empty"));
            return;
        }

        String mailId = data.popMailFromMailbox();
        if (mailId == null) return;

        // 标记为已读
        data.addMailFlag(mailId);

        MailEntry entry = MailRegistry.get(mailId);
        if (entry == null) {
            LOGGER.warn("Mail '{}' not found in registry, skipping", mailId);
            // 自动尝试下一封
            if (data.hasMailInMailbox()) {
                openNextMail(player);
            }
            return;
        }

        // 处理文本替换
        String text = entry.getText();
        text = text.replace("@", player.getName().getString());

        // 构建附件列表 & 发放物品到玩家背包
        List<OpenMailPayload.ItemAttachment> items = new ArrayList<>();
        for (MailEntry.AttachedItem ai : entry.getAttachedItems()) {
            items.add(new OpenMailPayload.ItemAttachment(ai.id(), ai.count()));
            // 服务端直接给玩家物品
            net.minecraft.resources.ResourceLocation itemRL = net.minecraft.resources.ResourceLocation.parse(ai.id());
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemRL);
            if (item != net.minecraft.world.item.Items.AIR) {
                net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, ai.count());
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
        }

        // 处理金钱附件（服务端直接加钱 + 同步客户端）
        int money = entry.getMoney();
        if (money > 0) {
            data.addMoney(money);
            PlayerDataEventHandler.syncPlayerData(player, data);
        }

        // 配方学习
        String learnedRecipe = entry.getLearnedRecipe() != null ? entry.getLearnedRecipe() : "";
        String cookingOrCrafting = "";
        if (!learnedRecipe.isEmpty()) {
            cookingOrCrafting = entry.isRecipeIsCooking() ? "cooking" : "crafting";
            data.unlockRecipe(learnedRecipe);
        }

        boolean hasQuest = entry.getQuestId() != null || entry.getSpecialOrderId() != null;

        // 邮件附带任务时，服务端接受该任务
        if (entry.getQuestId() != null && !entry.getQuestId().isEmpty()) {
            com.stardew.craft.quest.QuestManager qm = com.stardew.craft.quest.QuestManager.of(player);
            if (qm != null) {
                qm.acceptQuest(entry.getQuestId(), player);
            }
        }

        int remaining = data.getMailbox().size();

        OpenMailPayload payload = new OpenMailPayload(
                mailId, text, entry.getBackground(),
                entry.getTextColor() != null ? entry.getTextColor() : "",
                items, money, learnedRecipe, cookingOrCrafting,
                hasQuest, remaining
        );

        PacketDistributor.sendToPlayer(player, payload);
    }

    /**
     * 夜间结算：将所有玩家的 mailForTomorrow 移入 mailbox。
     * 应在新一天开始时调用。
     */
    public static void deliverAllTomorrowMail(net.minecraft.server.MinecraftServer server) {
        PlayerDataManager manager = PlayerDataManager.get();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerStardewData data = manager.getOrCreateData(player.getUUID());
            data.deliverTomorrowMail();
        }
    }
}
