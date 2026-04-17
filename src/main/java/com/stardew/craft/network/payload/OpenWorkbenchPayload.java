package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.workbench.WorkbenchType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: open the workbench screen.
 */
@SuppressWarnings("null")
public record OpenWorkbenchPayload(int typeId) implements CustomPacketPayload {

    public static final Type<OpenWorkbenchPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_workbench"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenWorkbenchPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT, OpenWorkbenchPayload::typeId,
            OpenWorkbenchPayload::new
        );

    public OpenWorkbenchPayload(WorkbenchType type) {
        this(type.getId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenWorkbenchPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenWorkbenchPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;
        WorkbenchType wbType = WorkbenchType.fromId(payload.typeId());
        mc.setScreen(new com.stardew.craft.client.gui.WorkbenchScreen(wbType));
    }
}
