package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Recipient thanks, whose close advances the non-animation Secret Santa sequence. */
@SuppressWarnings("null")
public record OpenWinterStarRecipientThanksPayload(String npcId, String itemName) implements CustomPacketPayload {
    public static final Type<OpenWinterStarRecipientThanksPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_winter_star_recipient_thanks"));
    public static final StreamCodec<FriendlyByteBuf, OpenWinterStarRecipientThanksPayload> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> { buf.writeUtf(value.npcId(), 64); buf.writeUtf(value.itemName(), 256); },
        buf -> new OpenWinterStarRecipientThanksPayload(buf.readUtf(64), buf.readUtf(256)));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenWinterStarRecipientThanksPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenWinterStarRecipientThanksPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;
        String text = Component.translatable("stardewcraft.festival.winter_star.recipient_thanks", payload.itemName()).getString();
        com.stardew.craft.client.gui.common.StardewNpcDialogueScreen screen =
            new com.stardew.craft.client.gui.common.StardewNpcDialogueScreen(payload.npcId(), text, 0)
                .withAfterClose(() -> PacketDistributor.sendToServer(new WinterStarRecipientThanksClosedPayload()));
        mc.setScreen(screen);
    }
}
