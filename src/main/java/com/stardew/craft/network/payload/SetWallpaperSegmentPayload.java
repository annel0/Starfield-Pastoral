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

import javax.annotation.Nullable;

/**
 * Client→Server packet to set or clear the segment override on a wallpaper block.
 * segment: 0=bottom, 1=middle, 2=top, -1=auto (compute from column).
 * Supports optional region mode via cornerA/cornerB.
 */
public record SetWallpaperSegmentPayload(
    BlockPos pos, int segment,
    boolean regionMode, @Nullable BlockPos cornerA, @Nullable BlockPos cornerB
) implements CustomPacketPayload {

    /** Single-block constructor (backward compatible). */
    public SetWallpaperSegmentPayload(BlockPos pos, int segment) {
        this(pos, segment, false, null, null);
    }

    /** Region constructor. */
    public static SetWallpaperSegmentPayload region(BlockPos pos, int segment, BlockPos cornerA, BlockPos cornerB) {
        return new SetWallpaperSegmentPayload(pos, segment, true, cornerA, cornerB);
    }

    @SuppressWarnings("null")
    public static final Type<SetWallpaperSegmentPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "set_wallpaper_segment"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, SetWallpaperSegmentPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBlockPos(payload.pos());
            buf.writeInt(payload.segment());
            buf.writeBoolean(payload.regionMode());
            if (payload.regionMode() && payload.cornerA() != null && payload.cornerB() != null) {
                buf.writeBlockPos(payload.cornerA());
                buf.writeBlockPos(payload.cornerB());
            }
        },
        buf -> {
            BlockPos p = buf.readBlockPos();
            int seg = buf.readInt();
            boolean region = buf.readBoolean();
            BlockPos cA = null, cB = null;
            if (region) {
                cA = buf.readBlockPos();
                cB = buf.readBlockPos();
            }
            return new SetWallpaperSegmentPayload(p, seg, region, cA, cB);
        }
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

            if (payload.regionMode() && payload.cornerA() != null && payload.cornerB() != null) {
                // Validate region size
                int dx = Math.abs(payload.cornerA().getX() - payload.cornerB().getX()) + 1;
                int dy = Math.abs(payload.cornerA().getY() - payload.cornerB().getY()) + 1;
                int dz = Math.abs(payload.cornerA().getZ() - payload.cornerB().getZ()) + 1;
                if ((long) dx * dy * dz > 125000) return;

                BlockPos min = new BlockPos(
                    Math.min(payload.cornerA().getX(), payload.cornerB().getX()),
                    Math.min(payload.cornerA().getY(), payload.cornerB().getY()),
                    Math.min(payload.cornerA().getZ(), payload.cornerB().getZ())
                );
                BlockPos max = new BlockPos(
                    Math.max(payload.cornerA().getX(), payload.cornerB().getX()),
                    Math.max(payload.cornerA().getY(), payload.cornerB().getY()),
                    Math.max(payload.cornerA().getZ(), payload.cornerB().getZ())
                );
                for (BlockPos bp : BlockPos.betweenClosed(min, max)) {
                    if (player.level().getBlockEntity(bp) instanceof DecorBlockEntity decorBe) {
                        decorBe.setSegmentOverride(payload.segment());
                    }
                }
            } else {
                if (player.level().getBlockEntity(payload.pos()) instanceof DecorBlockEntity decorBe) {
                    decorBe.setSegmentOverride(payload.segment());
                }
            }
        });
    }
}
