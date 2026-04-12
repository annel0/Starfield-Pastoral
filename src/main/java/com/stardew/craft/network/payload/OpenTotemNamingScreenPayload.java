package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.TotemNamingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record OpenTotemNamingScreenPayload(
        long blockPos,
        String currentName,
        String totemTypeId,
        int poleId
) implements CustomPacketPayload {

    public static final Type<OpenTotemNamingScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_totem_naming_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTotemNamingScreenPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, OpenTotemNamingScreenPayload::blockPos,
                    ByteBufCodecs.STRING_UTF8, OpenTotemNamingScreenPayload::currentName,
                    ByteBufCodecs.STRING_UTF8, OpenTotemNamingScreenPayload::totemTypeId,
                    ByteBufCodecs.INT, OpenTotemNamingScreenPayload::poleId,
                    OpenTotemNamingScreenPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenTotemNamingScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(OpenTotemNamingScreenPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.setScreen(new TotemNamingScreen(payload));
    }
}
