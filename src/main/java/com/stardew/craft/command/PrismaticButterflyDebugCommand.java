package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.mastery.PrismaticButterflyService;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public final class PrismaticButterflyDebugCommand {
    private PrismaticButterflyDebugCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
            .then(Commands.literal("butterfly")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("spawn")
                    .then(Commands.argument("pos", Vec3Argument.vec3())
                        .executes(PrismaticButterflyDebugCommand::spawnAt)))
                .then(Commands.literal("spawn_area")
                    .executes(PrismaticButterflyDebugCommand::spawnInArea))));
    }

    private static int spawnAt(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("This command requires a player."));
            return 0;
        }
        Vec3 pos = Vec3Argument.getVec3(context, "pos");
        if (!PrismaticButterflyService.spawnAt(player, pos)) {
            context.getSource().sendFailure(Component.literal("Failed to spawn prismatic butterfly."));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("Spawned prismatic butterfly at " + pos.x + " " + pos.y + " " + pos.z), false);
        return 1;
    }

    private static int spawnInArea(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("This command requires a player."));
            return 0;
        }
        var failure = PrismaticButterflyService.spawnForDebug(player);
        if (failure.isPresent()) {
            context.getSource().sendFailure(Component.literal("Failed to spawn prismatic butterfly: " + failure.get()));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("Spawned prismatic butterfly in current/daily area."), false);
        return 1;
    }
}