package com.stardew.craft.network.overnight;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screens.Screen;
import com.stardew.craft.client.gui.overnight.ShippingMenuScreen;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientOvernightHandler {
    public static void startSequence(OvernightSettlementPayload payload) {
        // Build the stack and execute
        List<Screen> screenStack = new java.util.ArrayList<>();
        
        if (!payload.shippedItems().isEmpty()) {
            screenStack.add(new ShippingMenuScreen(payload.shippedItems(), screenStack));
        }

        // Add level up menus
        for (OvernightSettlementPayload.LevelUpData levelData : payload.levelUps()) {
            screenStack.add(0, new com.stardew.craft.client.gui.overnight.LevelUpMenuScreen(levelData, screenStack));
        }

        if (!screenStack.isEmpty()) {
            Minecraft.getInstance().setScreen(screenStack.remove(0));
        }
    }
}
