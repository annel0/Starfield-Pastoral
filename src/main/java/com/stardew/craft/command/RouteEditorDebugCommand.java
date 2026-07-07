package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.item.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class RouteEditorDebugCommand {
    private RouteEditorDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("route_editor")
                .then(Commands.literal("give")
                    .executes(RouteEditorDebugCommand::give))));
    }

    private static int give(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("Must be a player."));
            return 0;
        }
        ItemStack stack = new ItemStack(ModItems.ROUTE_EDITOR_WAND.get());
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        context.getSource().sendSuccess(() -> Component.literal("Gave route editor wand."), false);
        return 1;
    }
}
