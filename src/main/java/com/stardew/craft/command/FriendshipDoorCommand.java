package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.stardew.craft.blockentity.FriendshipDoorBlockEntity;
import com.stardew.craft.item.FriendshipDoorItem;
import com.stardew.craft.npc.data.NpcDataRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

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
                        .then(Commands.argument("binding", StringArgumentType.greedyString())
                            .suggests(FriendshipDoorCommand::suggestBinding)
                            .executes(FriendshipDoorCommand::give))))));
    }

    private static int give(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        DoorBinding binding = parseBinding(StringArgumentType.getString(context, "binding"));
        List<String> npcIds = binding.npcIds();
        if (npcIds.isEmpty()) {
            context.getSource().sendFailure(Component.literal("NPC id list cannot be empty."));
            return 0;
        }

        for (ServerPlayer player : targets) {
            ItemStack stack = FriendshipDoorItem.create(npcIds, binding.requiredPoints());
            boolean inserted = player.getInventory().add(stack);
            if (!inserted && !stack.isEmpty()) {
                player.drop(stack, false);
            }
        }

        int count = targets.size();
        context.getSource().sendSuccess(() -> Component.literal("Gave " + count + " friendship door(s) for " + String.join(",", npcIds) + " (" + binding.requiredPoints() + " points)."), true);
        return count;
    }

    private static DoorBinding parseBinding(String rawBinding) {
        String binding = rawBinding == null ? "" : rawBinding.trim();
        int requiredPoints = FriendshipDoorBlockEntity.DEFAULT_REQUIRED_POINTS;

        int lastSpace = binding.lastIndexOf(' ');
        if (lastSpace >= 0) {
            String tail = binding.substring(lastSpace + 1).trim();
            try {
                requiredPoints = Math.max(0, Integer.parseInt(tail));
                binding = binding.substring(0, lastSpace).trim();
            } catch (NumberFormatException ignored) {
            }
        }

        return new DoorBinding(FriendshipDoorBlockEntity.parseNpcIds(binding), requiredPoints);
    }

    private static CompletableFuture<Suggestions> suggestBinding(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
        int start = Math.max(remaining.lastIndexOf(','), remaining.lastIndexOf(' ')) + 1;
        SuggestionsBuilder replacement = builder.createOffset(builder.getStart() + start);
        for (String npcId : NpcDataRegistry.capabilities().keySet()) {
            replacement.suggest(npcId);
        }
        return replacement.buildFuture();
    }

    private record DoorBinding(List<String> npcIds, int requiredPoints) {
    }
}