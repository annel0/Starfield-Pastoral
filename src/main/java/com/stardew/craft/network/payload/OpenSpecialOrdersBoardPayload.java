package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenSpecialOrdersBoardPayload(CompoundTag data) implements CustomPacketPayload {
    public static final Type<OpenSpecialOrdersBoardPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_special_orders_board"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSpecialOrdersBoardPayload> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, OpenSpecialOrdersBoardPayload::data, OpenSpecialOrdersBoardPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenSpecialOrdersBoardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenSpecialOrdersBoardPayload payload) {
        com.stardew.craft.client.gui.specialorder.ClientSpecialOrderBoardData.replace(payload.data());
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new com.stardew.craft.client.gui.specialorder.SpecialOrdersBoardScreen(payload.data()));
        }
    }
}
