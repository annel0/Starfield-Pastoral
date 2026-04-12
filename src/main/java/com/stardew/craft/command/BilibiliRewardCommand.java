package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * /stardewcraft bilibili — 领取 B 站关注奖励（彩虹猫之刃，一人一把）。
 */
@SuppressWarnings("null")
public class BilibiliRewardCommand {

    private static final String BILIBILI_URL = "https://space.bilibili.com/259427053";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardewcraft")
                .then(Commands.literal("bilibili")
                    .executes(ctx -> claimReward(ctx.getSource()))
                )
        );
    }

    private static int claimReward(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("仅玩家可执行此命令"));
            return 0;
        }

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.isBilibiliRewardClaimed()) {
            player.sendSystemMessage(Component.literal("§e你已经领取过 B 站关注奖励了！"));
            return 0;
        }

        // 发放彩虹猫之刃
        ItemStack meowmere = new ItemStack(ModItems.MEOWMERE.get());
        if (!player.getInventory().add(meowmere)) {
            player.drop(meowmere, false);
        }
        data.setBilibiliRewardClaimed(true);

        // 打开 B 站页面的提示
        MutableComponent urlMsg = Component.literal("§a[点击访问作者B站主页]")
            .setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BILIBILI_URL))
                .withUnderlined(true)
                .withColor(ChatFormatting.GREEN));

        player.sendSystemMessage(Component.literal("§6§l✦ §e你获得了 §d§l彩虹猫之刃§e！感谢关注！§6§l ✦"));
        player.sendSystemMessage(urlMsg);

        return 1;
    }
}
