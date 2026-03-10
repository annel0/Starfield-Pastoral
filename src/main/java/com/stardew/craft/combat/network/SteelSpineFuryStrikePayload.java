package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.SkillEffectsClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SteelSpineFuryStrikePayload(boolean strong) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<SteelSpineFuryStrikePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "steel_spine_fury_strike")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, SteelSpineFuryStrikePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        SteelSpineFuryStrikePayload::strong,
        SteelSpineFuryStrikePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SteelSpineFuryStrikePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                SkillEffectsClient.playSteelSpineFury(mc.player, payload.strong());
            }
        });
    }
}
