package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.DecorAnchorBlockEntity;
import com.stardew.craft.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateDecorAnchorPayload(
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
    public static final Type<UpdateDecorAnchorPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "update_decor_anchor"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, UpdateDecorAnchorPayload> STREAM_CODEC = StreamCodec.of(
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
        buf -> new UpdateDecorAnchorPayload(
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

    @SuppressWarnings("null")
    public static void handle(UpdateDecorAnchorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (player.distanceToSqr(payload.targetPos().getX() + 0.5, payload.targetPos().getY() + 0.5, payload.targetPos().getZ() + 0.5) > 100.0) {
                player.sendSystemMessage(Component.literal("Decor anchor edit failed: target is too far away."));
                return;
            }

            if (!(player.level().getBlockEntity(payload.targetPos()) instanceof DecorAnchorBlockEntity anchor)) {
                player.sendSystemMessage(Component.literal("Decor anchor edit failed: target is not a decor anchor."));
                return;
            }

            anchor.setEditorState(
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
            );
        });
    }
}
