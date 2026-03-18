package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.DecorAnchorEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenDecorAnchorEditorPayload(
    BlockPos targetPos,
    String styleId,
    float offsetX,
    float offsetY,
    float offsetZ,
    float rotX,
    float rotY,
    float rotZ,
    float scaleX,
    float scaleY,
    float scaleZ
) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<OpenDecorAnchorEditorPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_decor_anchor_editor"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, OpenDecorAnchorEditorPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBlockPos(payload.targetPos());
            buf.writeUtf(payload.styleId(), 32);
            buf.writeFloat(payload.offsetX());
            buf.writeFloat(payload.offsetY());
            buf.writeFloat(payload.offsetZ());
            buf.writeFloat(payload.rotX());
            buf.writeFloat(payload.rotY());
            buf.writeFloat(payload.rotZ());
            buf.writeFloat(payload.scaleX());
            buf.writeFloat(payload.scaleY());
            buf.writeFloat(payload.scaleZ());
        },
        buf -> new OpenDecorAnchorEditorPayload(
            buf.readBlockPos(),
            buf.readUtf(32),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDecorAnchorEditorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            mc.setScreen(new DecorAnchorEditorScreen(
                payload.targetPos(),
                payload.styleId(),
                payload.offsetX(),
                payload.offsetY(),
                payload.offsetZ(),
                payload.rotX(),
                payload.rotY(),
                payload.rotZ(),
                payload.scaleX(),
                payload.scaleY(),
                payload.scaleZ()
            ));
        });
    }
}
