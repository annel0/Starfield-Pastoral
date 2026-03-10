package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.TemplarJudgementImpactClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TemplarJudgementImpactPayload(int entityId) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<TemplarJudgementImpactPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "templar_judgement_impact")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, TemplarJudgementImpactPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        TemplarJudgementImpactPayload::entityId,
        TemplarJudgementImpactPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TemplarJudgementImpactPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> TemplarJudgementImpactClient.playImpact(payload.entityId()));
    }
}
