package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.museum.MuseumDonationData;
import com.stardew.craft.network.MuseumDonationSyncPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * 博物馆调试命令（用于模拟捐赠）
 */
@SuppressWarnings("null")
public class MuseumDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("stardew")
                .then(Commands.literal("museum")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("donate")
                .then(Commands.argument("item", ItemArgument.item(buildContext))
                                        .executes(MuseumDebugCommand::donateItem)))));
    }

    private static int donateItem(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        Item item = ItemArgument.getItem(context, "item").getItem();
        if (!(item instanceof IStardewItem stardewItem)) {
            context.getSource().sendFailure(Component.translatable("stardewcraft.command.museum.donate.invalid"));
            return 0;
        }

        String typeKey = stardewItem.getItemTypeKey();
        if (!"stardewcraft.type.mineral".equals(typeKey) && !"stardewcraft.type.artifact".equals(typeKey)) {
            context.getSource().sendFailure(Component.translatable("stardewcraft.command.museum.donate.invalid"));
            return 0;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        boolean added = data.donate(id.toString());
        ItemStack stack = new ItemStack(item);
        Component itemName = stack.getHoverName();

    PacketDistributor.sendToAllPlayers(new MuseumDonationSyncPacket(List.copyOf(data.getDonatedItems())));

    if (added) {
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.museum.donate.success", itemName
        ), false);
    } else {
        context.getSource().sendSuccess(() -> Component.translatable(
            "stardewcraft.command.museum.donate.already", itemName
        ), false);
    }

        return 1;
    }
}
