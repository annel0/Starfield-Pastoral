package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record OpenFairWheelGamePayload(int starTokens, int luckLevel) implements CustomPacketPayload {
    public static final Type<OpenFairWheelGamePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_fair_wheel_game"));

    public static final StreamCodec<ByteBuf, OpenFairWheelGamePayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,
            OpenFairWheelGamePayload::starTokens,
            ByteBufCodecs.INT,
            OpenFairWheelGamePayload::luckLevel,
            OpenFairWheelGamePayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenFairWheelGamePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenFairWheelGamePayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        mc.setScreen(new com.stardew.craft.client.gui.festival.FairWheelGameScreen(
            payload.starTokens(),
            payload.luckLevel()
        ));
    }
}
