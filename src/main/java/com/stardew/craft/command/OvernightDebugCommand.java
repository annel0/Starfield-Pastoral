package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.network.overnight.OvernightSettlementPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public class OvernightDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stardew")
                .then(Commands.literal("night")
                .requires(source -> source.hasPermission(2))
                .executes(OvernightDebugCommand::testNight)));
    }

    private static int testNight(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            List<OvernightSettlementPayload.ShippedItem> items = new ArrayList<>();
            items.add(new OvernightSettlementPayload.ShippedItem(new ItemStack(Items.WHEAT, 12), 0, 25));
            items.add(new OvernightSettlementPayload.ShippedItem(new ItemStack(Items.MELON, 4), 0, 60));
            items.add(new OvernightSettlementPayload.ShippedItem(new ItemStack(Items.SALMON, 2), 2, 75));
            items.add(new OvernightSettlementPayload.ShippedItem(new ItemStack(Items.IRON_INGOT, 1), 3, 120));
            items.add(new OvernightSettlementPayload.ShippedItem(new ItemStack(Items.BONE, 5), 4, 10));

            List<OvernightSettlementPayload.LevelUpData> levelUps = new ArrayList<>();
            levelUps.add(new OvernightSettlementPayload.LevelUpData(0, 4)); // Farming 4
            levelUps.add(new OvernightSettlementPayload.LevelUpData(0, 5)); // Farming 5 (Profession)

            PacketDistributor.sendToPlayer(player, new OvernightSettlementPayload(items, levelUps));
        }
        return 1;
    }
}
