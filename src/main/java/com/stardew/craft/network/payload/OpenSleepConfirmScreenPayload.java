package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.common.StardewConfirmDialogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenSleepConfirmScreenPayload(int currentMinute) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<OpenSleepConfirmScreenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_sleep_confirm_screen"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, OpenSleepConfirmScreenPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeVarInt(payload.currentMinute()),
        buf -> new OpenSleepConfirmScreenPayload(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenSleepConfirmScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenSleepConfirmScreenPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        mc.setScreen(StardewConfirmDialogScreen.createSleepConfirm(payload.currentMinute()));
    }
}
