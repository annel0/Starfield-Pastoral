package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: result of a sell attempt.
 */
@SuppressWarnings("null")
public record ShopSellResultPayload(
    boolean success,
    int     newMoney,
    int     inventorySlot,
    int     qtyRemoved,
    int     earned
) implements CustomPacketPayload {

    public static final Type<ShopSellResultPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "shop_sell_result"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopSellResultPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL, ShopSellResultPayload::success,
            ByteBufCodecs.INT,  ShopSellResultPayload::newMoney,
            ByteBufCodecs.INT,  ShopSellResultPayload::inventorySlot,
            ByteBufCodecs.INT,  ShopSellResultPayload::qtyRemoved,
            ByteBufCodecs.INT,  ShopSellResultPayload::earned,
            ShopSellResultPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopSellResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(ShopSellResultPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.screen instanceof com.stardew.craft.client.gui.ShopScreen shop) {
            shop.onSellResult(payload);
        }
    }
}
