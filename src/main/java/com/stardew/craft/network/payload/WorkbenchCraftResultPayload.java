package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: result of a workbench craft.
 */
@SuppressWarnings("null")
public record WorkbenchCraftResultPayload(
    boolean success,
    int remainingMaterial,
    int remainingBonus,
    int craftedCount
) implements CustomPacketPayload {

    public static final Type<WorkbenchCraftResultPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "workbench_craft_result"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchCraftResultPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL, WorkbenchCraftResultPayload::success,
            ByteBufCodecs.INT,  WorkbenchCraftResultPayload::remainingMaterial,
            ByteBufCodecs.INT,  WorkbenchCraftResultPayload::remainingBonus,
            ByteBufCodecs.INT,  WorkbenchCraftResultPayload::craftedCount,
            WorkbenchCraftResultPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(WorkbenchCraftResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(WorkbenchCraftResultPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.screen instanceof com.stardew.craft.client.gui.WorkbenchScreen screen) {
            screen.onCraftResult(payload);
        }
    }
}
