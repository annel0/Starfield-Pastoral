package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.SkillEffectsClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SteelSpineFuryHitPayload() implements CustomPacketPayload {

    public SteelSpineFuryHitPayload {
    }

    @SuppressWarnings("null")
    public static final Type<SteelSpineFuryHitPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "steel_spine_fury_hit")
    );

    public static final StreamCodec<ByteBuf, SteelSpineFuryHitPayload> STREAM_CODEC = StreamCodec.unit(new SteelSpineFuryHitPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SteelSpineFuryHitPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(SteelSpineFuryHitPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            SkillEffectsClient.playSteelSpineFuryHit(mc.player);
        }
    }
}
