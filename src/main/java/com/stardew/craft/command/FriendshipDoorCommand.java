package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.blockentity.FriendshipDoorBlockEntity;
import com.stardew.craft.item.FriendshipDoorItem;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("null")
public final class FriendshipDoorCommand {
    private FriendshipDoorCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("friendship_door")
                .then(Commands.literal("give")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("npcId", StringArgumentType.word())
                            .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(NpcDataRegistry.capabilities().keySet(), builder))
                            .executes(ctx -> give(ctx, FriendshipDoorBlockEntity.DEFAULT_REQUIRED_POINTS))
                            .then(Commands.argument("requiredPoints", IntegerArgumentType.integer(0))
                                .executes(ctx -> give(ctx, IntegerArgumentType.getInteger(ctx, "requiredPoints")))))))));
    }

    private static int give(CommandContext<CommandSourceStack> context, int requiredPoints) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        List<String> npcIds = FriendshipDoorBlockEntity.parseNpcIds(StringArgumentType.getString(context, "npcId"));
        if (npcIds.isEmpty()) {
            context.getSource().sendFailure(Component.literal("NPC id list cannot be empty."));
            return 0;
        }

        for (ServerPlayer player : targets) {
            ItemStack stack = FriendshipDoorItem.create(npcIds, requiredPoints);
            boolean inserted = player.getInventory().add(stack);
            if (!inserted && !stack.isEmpty()) {
                player.drop(stack, false);
            }
        }

        int count = targets.size();
        context.getSource().sendSuccess(() -> Component.literal("Gave " + count + " friendship door(s) for " + String.join(",", npcIds) + " (" + requiredPoints + " points)."), true);
        return count;
    }
}