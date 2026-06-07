package com.stardew.craft.specialorder;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.network.payload.SpecialOrderDropBoxHintPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Set;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class SpecialOrderDropBoxService {

    private SpecialOrderDropBoxService() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncHints(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())) {
            return;
        }
        SpecialOrderDropBoxAnchor.at(event.getPos()).ifPresent(anchor -> {
            if (!activeDropBoxIds(player).contains(anchor.dropBoxId())) {
                return;
            }
            player.displayClientMessage(Component.translatable(
                "stardewcraft.special_orders.dropbox.placeholder",
                Component.translatable(anchor.translationKey())), true);
            event.setCanceled(true);
        });
    }

    public static void syncHints(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SpecialOrderDropBoxHintPayload(activeDropBoxIds(player)));
    }

    public static void syncHints(ServerPlayer player, Collection<String> activeDropBoxIds) {
        PacketDistributor.sendToPlayer(player, new SpecialOrderDropBoxHintPayload(activeDropBoxIds));
    }

    public static Set<String> activeDropBoxIds(ServerPlayer player) {
        return Set.of();
    }
}
