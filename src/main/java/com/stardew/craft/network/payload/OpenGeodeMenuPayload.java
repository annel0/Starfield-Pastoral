package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.GeodeMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

/**
 * Server → Client: open the geode processing screen.
 */
@SuppressWarnings("null")
public record OpenGeodeMenuPayload() implements CustomPacketPayload {

    private static final ResourceLocation ID = Objects.requireNonNull(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_geode_menu"));

    public static final Type<OpenGeodeMenuPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, OpenGeodeMenuPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {},
        buf -> new OpenGeodeMenuPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenGeodeMenuPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient());
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.setScreen(new GeodeMenuScreen());
    }
}
