package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TremorBlockPayload(float x, float y, float z, int blockStateId, float ySpeed)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<TremorBlockPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "tremor_block")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, TremorBlockPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        TremorBlockPayload::x,
        ByteBufCodecs.FLOAT,
        TremorBlockPayload::y,
        ByteBufCodecs.FLOAT,
        TremorBlockPayload::z,
        ByteBufCodecs.VAR_INT,
        TremorBlockPayload::blockStateId,
        ByteBufCodecs.FLOAT,
        TremorBlockPayload::ySpeed,
        TremorBlockPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TremorBlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(TremorBlockPayload payload) {
        var mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            return;
        }
        var level = java.util.Objects.requireNonNull(mc.level);
        var state = Block.stateById(payload.blockStateId());
        if (state == null) {
            return;
        }
        level.addParticle(
            new BlockParticleOption(java.util.Objects.requireNonNull(ParticleTypes.BLOCK), java.util.Objects.requireNonNull(state)),
            payload.x(), payload.y(), payload.z(),
            0.0, payload.ySpeed(), 0.0
        );
    }
}
