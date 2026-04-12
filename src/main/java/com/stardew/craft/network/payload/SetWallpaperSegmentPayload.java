package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.DecorBlockEntity;
import com.stardew.craft.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client→Server packet to set or clear the segment override on a wallpaper block.
 * segment: 0=bottom, 1=middle, 2=top, -1=auto (compute from column).
 */
public record SetWallpaperSegmentPayload(BlockPos pos, int segment) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<SetWallpaperSegmentPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "set_wallpaper_segment"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, SetWallpaperSegmentPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBlockPos(payload.pos());
            buf.writeInt(payload.segment());
        },
        buf -> new SetWallpaperSegmentPayload(buf.readBlockPos(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(SetWallpaperSegmentPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            // Validate segment range
            if (payload.segment() < -1 || payload.segment() > 2) return;

            // Validate distance
            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) > 100.0) return;

            // Validate holding paintbrush
            boolean holdingPaintbrush = player.getMainHandItem().is(ModItems.PAINTBRUSH.get())
                || player.getOffhandItem().is(ModItems.PAINTBRUSH.get());
            if (!holdingPaintbrush) return;

            if (player.level().getBlockEntity(payload.pos()) instanceof DecorBlockEntity decorBe) {
                decorBe.setSegmentOverride(payload.segment());
            }
        });
    }
}
