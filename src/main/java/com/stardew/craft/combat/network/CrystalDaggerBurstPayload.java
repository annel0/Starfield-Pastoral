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

public record CrystalDaggerBurstPayload() implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<CrystalDaggerBurstPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "crystal_dagger_burst")
    );

    public static final StreamCodec<ByteBuf, CrystalDaggerBurstPayload> STREAM_CODEC =
        StreamCodec.unit(new CrystalDaggerBurstPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CrystalDaggerBurstPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                SkillEffectsClient.playSkillEffects("crystal_dagger_burst", mc.player);
            }
        });
    }
}
