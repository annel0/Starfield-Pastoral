package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * S→C: 服务端要求客户端打开农场选择/命名 GUI。
 * 在巫师任务完成后首次传送前触发。
 */
@SuppressWarnings("null")
public record OpenFarmSelectionPayload() implements CustomPacketPayload {

    public static final Type<OpenFarmSelectionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_farm_selection"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenFarmSelectionPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenFarmSelectionPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenFarmSelectionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(OpenFarmSelectionPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;
        mc.setScreen(new com.stardew.craft.client.gui.FarmSelectionScreen());
    }
}
