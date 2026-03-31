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

public record BrokenTridentThrustStrikePayload() implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<BrokenTridentThrustStrikePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "broken_trident_thrust_strike")
    );

    public static final StreamCodec<ByteBuf, BrokenTridentThrustStrikePayload> STREAM_CODEC =
        StreamCodec.unit(new BrokenTridentThrustStrikePayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BrokenTridentThrustStrikePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(BrokenTridentThrustStrikePayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            SkillEffectsClient.playSkillEffects("fishcatch_thrust", mc.player);
        }
    }
}
