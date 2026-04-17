package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.render.ClientStarterChestState;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: show or hide the starter chest hint at a given position.
 * show=true → display golden hint at pos; show=false → remove hint.
 */
public record StarterChestHintPayload(BlockPos pos, boolean show) implements CustomPacketPayload {

    public static final Type<StarterChestHintPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "starter_chest_hint"));

    public static final StreamCodec<ByteBuf, StarterChestHintPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StarterChestHintPayload::pos,
            ByteBufCodecs.BOOL, StarterChestHintPayload::show,
            StarterChestHintPayload::new);

    @SuppressWarnings("null")
    public static void handle(StarterChestHintPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.show) {
                ClientStarterChestState.setHintPos(payload.pos);
            } else {
                ClientStarterChestState.clear();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
