package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.utility.CushionBlock;
import com.stardew.craft.block.utility.OfficeChair2Block;
import com.stardew.craft.block.utility.OfficeStoolBlock;
import com.stardew.craft.block.utility.SofaBlock;
import com.stardew.craft.block.utility.WoodenChestColorPalette;
import com.stardew.craft.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public record ApplySofaColorPayload(BlockPos targetPos, int colorIndex) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<ApplySofaColorPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "apply_sofa_color"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, ApplySofaColorPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBlockPos(payload.targetPos());
            buf.writeVarInt(payload.colorIndex());
        },
        buf -> new ApplySofaColorPayload(buf.readBlockPos(), buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(ApplySofaColorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            boolean holdingPaintbrush = player.getMainHandItem().is(ModItems.PAINTBRUSH.get())
                || player.getOffhandItem().is(ModItems.PAINTBRUSH.get());
            if (!holdingPaintbrush) {
                return;
            }

            if (player.distanceToSqr(payload.targetPos().getX() + 0.5D, payload.targetPos().getY() + 0.5D, payload.targetPos().getZ() + 0.5D) > 100.0D) {
                return;
            }

            BlockState state = player.level().getBlockState(payload.targetPos());
            int clamped = WoodenChestColorPalette.clampIndex(payload.colorIndex());
            if (clamped < 0) {
                clamped = 0;
            }

            if (state.getBlock() instanceof SofaBlock) {
                for (BlockPos sofaPos : collectConnectedSofas(payload.targetPos(), player.level())) {
                    BlockState sofaState = player.level().getBlockState(sofaPos);
                    if (!(sofaState.getBlock() instanceof SofaBlock) || !sofaState.hasProperty(SofaBlock.COLOR)) {
                        continue;
                    }
                    BlockState updated = sofaState.setValue(SofaBlock.COLOR, clamped);
                    if (updated != sofaState) {
                        player.level().setBlock(sofaPos, updated, 3);
                    }
                }
                return;
            }

            if (state.getBlock() instanceof CushionBlock && state.hasProperty(CushionBlock.COLOR)) {
                BlockState updated = state.setValue(CushionBlock.COLOR, clamped);
                if (updated != state) {
                    player.level().setBlock(payload.targetPos(), updated, 3);
                }
                return;
            }

            if (state.getBlock() instanceof OfficeStoolBlock && state.hasProperty(OfficeStoolBlock.COLOR)) {
                BlockState updated = state.setValue(OfficeStoolBlock.COLOR, clamped);
                if (updated != state) {
                    player.level().setBlock(payload.targetPos(), updated, 3);
                }
                return;
            }

            if (state.getBlock() instanceof OfficeChair2Block && state.hasProperty(OfficeChair2Block.COLOR)) {
                BlockState updated = state.setValue(OfficeChair2Block.COLOR, clamped);
                if (updated != state) {
                    player.level().setBlock(payload.targetPos(), updated, 3);
                }
            }
        });
    }

    @SuppressWarnings("null")
    private static Set<BlockPos> collectConnectedSofas(BlockPos start, net.minecraft.world.level.Level level) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) {
                continue;
            }

            BlockState currentState = level.getBlockState(current);
            if (!(currentState.getBlock() instanceof SofaBlock)) {
                continue;
            }

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos next = current.relative(direction);
                if (visited.contains(next)) {
                    continue;
                }
                BlockState nextState = level.getBlockState(next);
                if (nextState.getBlock() instanceof SofaBlock) {
                    queue.add(next);
                }
            }
        }
        return visited;
    }
}
