package com.stardew.craft.network.overnight;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.screens.Screen;
import com.stardew.craft.client.gui.overnight.ShippingMenuScreen;
import com.stardew.craft.player.ProfessionType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ClientOvernightHandler {
    private static final Set<Integer> LOCAL_OVERNIGHT_PROFESSIONS = new HashSet<>();

    public static void beginSequence() {
        LOCAL_OVERNIGHT_PROFESSIONS.clear();
    }

    public static void recordLocalProfessionChoice(int professionId) {
        LOCAL_OVERNIGHT_PROFESSIONS.add(professionId);
    }

    public static boolean hasLocalProfession(ProfessionType profession) {
        return profession != null && LOCAL_OVERNIGHT_PROFESSIONS.contains(profession.getId());
    }

    public static void startSequence(OvernightSettlementPayload payload) {
        beginSequence();

        // Match vanilla ordering: level-up menus first (in newLevels order), shipping summary last.
        List<Screen> screenStack = new java.util.ArrayList<>();

        for (OvernightSettlementPayload.LevelUpData levelData : payload.levelUps()) {
            screenStack.add(new com.stardew.craft.client.gui.overnight.LevelUpMenuScreen(levelData, screenStack));
        }

        if (!payload.shippedItems().isEmpty()) {
            screenStack.add(new ShippingMenuScreen(payload.shippedItems(), screenStack));
        }

        if (!screenStack.isEmpty()) {
            Minecraft.getInstance().setScreen(screenStack.remove(0));
        }
    }
}
