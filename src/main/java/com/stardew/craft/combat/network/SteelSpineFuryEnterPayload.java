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

public record SteelSpineFuryEnterPayload() implements CustomPacketPayload {

    public SteelSpineFuryEnterPayload {
    }

    @SuppressWarnings("null")
    public static final Type<SteelSpineFuryEnterPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "steel_spine_fury_enter")
    );

    public static final StreamCodec<ByteBuf, SteelSpineFuryEnterPayload> STREAM_CODEC = StreamCodec.unit(new SteelSpineFuryEnterPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SteelSpineFuryEnterPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(SteelSpineFuryEnterPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            SkillEffectsClient.playSteelSpineFuryEnter(mc.player);
        }
    }
}
