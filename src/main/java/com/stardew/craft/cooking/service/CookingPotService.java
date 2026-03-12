package com.stardew.craft.cooking.service;

import com.stardew.craft.menu.CookingPotMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

public final class CookingPotService {
    private CookingPotService() {
    }

    @SuppressWarnings("null")
    public static void openForPlayer(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, playerEntity) -> new CookingPotMenu(containerId, playerInventory),
                Component.translatable("stardewcraft.cooking.title")
        ));
    }
}
