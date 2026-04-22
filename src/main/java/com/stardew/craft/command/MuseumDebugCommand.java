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
                        .then(Commands.literal("start")
                                .executes(MuseumDebugCommand::startDonationMode))
                        .then(Commands.literal("end")
                                .executes(MuseumDebugCommand::endDonationMode))
                        .then(Commands.literal("status")
                                .executes(MuseumDebugCommand::statusDonationMode))
                        .then(Commands.literal("donate")
                .then(Commands.argument("item", ItemArgument.item(buildContext))
                                        .executes(MuseumDebugCommand::donateItem)))));
    }

    private static int startDonationMode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        java.util.UUID playerId = player.getUUID();
        if (data.isDonationModeActive(playerId)) {
            context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.museum.mode.already_on"), false);
            return 1;
        }

        data.startDonationMode(playerId);
        context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.museum.mode.started"), false);
        return 1;
    }

    private static int endDonationMode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        java.util.UUID playerId = player.getUUID();
        if (!data.isDonationModeActive(playerId)) {
            context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.museum.mode.already_off"), false);
            return 1;
        }

        MuseumDonationData.EndSessionResult result = data.endDonationMode(playerId);
        if (!result.success()) {
            context.getSource().sendFailure(Component.translatable("stardewcraft.command.museum.mode.end_blocked", result.missingItems().size()));
            return 0;
        }

        syncAll(data, player);
        context.getSource().sendSuccess(() -> Component.translatable("stardewcraft.command.museum.mode.ended"), false);
        return 1;
    }

    private static int statusDonationMode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        java.util.UUID playerId = player.getUUID();
        context.getSource().sendSuccess(() -> Component.translatable(
                data.isDonationModeActive(playerId) ? "stardewcraft.command.museum.mode.status_on" : "stardewcraft.command.museum.mode.status_off"
        ), false);
        return 1;
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
        java.util.UUID playerId = player.getUUID();
        boolean added = data.donate(playerId, id.toString());
        ItemStack stack = new ItemStack(item);
        Component itemName = stack.getHoverName();

    syncAll(data, player);

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

    private static void syncAll(MuseumDonationData data, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new MuseumDonationSyncPacket(List.copyOf(data.getDonatedItems(player.getUUID()))));
    }
}
