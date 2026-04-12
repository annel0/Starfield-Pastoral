package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.communitycenter.menu.BundleMenu;
import com.stardew.craft.communitycenter.network.BundleSyncPayload;
import com.stardew.craft.communitycenter.state.CommunityCenterProgress;
import com.stardew.craft.communitycenter.state.CommunityCenterSavedData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

/**
 * Debug command for Community Center.
 * /cc open [areaId]   — open the bundle GUI for a given area
 * /cc status          — print progress summary
 * /cc reset           — reset all progress
 * /cc complete        — complete all bundles
 */
@SuppressWarnings("null")
public class CommunityCenterCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cc")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("open")
                        .executes(ctx -> openMenu(ctx, 1)) // default: Crafts Room
                        .then(Commands.argument("area", IntegerArgumentType.integer(0, 6))
                                .executes(ctx -> openMenu(ctx, IntegerArgumentType.getInteger(ctx, "area")))))
                .then(Commands.literal("status")
                        .executes(CommunityCenterCommand::showStatus))
                .then(Commands.literal("reset")
                        .executes(CommunityCenterCommand::resetAll))
                .then(Commands.literal("complete")
                        .executes(CommunityCenterCommand::completeAll)));
    }

    private static int openMenu(CommandContext<CommandSourceStack> ctx, int areaId) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        player.openMenu(new SimpleMenuProvider(
                (menuId, inv, p) -> new BundleMenu(menuId, inv, areaId),
                Component.translatable("stardewcraft.menu.community_center")
        ));

        // Send initial progress sync to client
        BundleSyncPayload.sendFullSync(player);

        ctx.getSource().sendSuccess(
                () -> Component.literal("Opened Community Center GUI for area " + areaId),
                false);
        return 1;
    }

    private static int showStatus(CommandContext<CommandSourceStack> ctx) {
        String summary = CommunityCenterProgress.getDebugSummary();
        ctx.getSource().sendSuccess(() -> Component.literal(summary), false);
        return 1;
    }

    private static int resetAll(CommandContext<CommandSourceStack> ctx) {
        CommunityCenterSavedData.get().resetAll();
        ctx.getSource().sendSuccess(
                () -> Component.literal("Community Center progress reset."), false);
        return 1;
    }

    private static int completeAll(CommandContext<CommandSourceStack> ctx) {
        CommunityCenterSavedData.get().completeAll();
        ctx.getSource().sendSuccess(
                () -> Component.literal("All Community Center bundles completed."), false);
        return 1;
    }
}
