package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: result of a purchase attempt.
 *
 * Fields:
 *   success   – whether the purchase was accepted
 *   newMoney  – player's money after the transaction
 *   itemId    – ResourceLocation string of the purchased item ("" on failure)
 *   quantity  – how many were bought (0 on failure)
 *   itemIndex – which slot in the shop list this was for (used for stock sync)
 */
@SuppressWarnings("null")
public record ShopPurchaseResultPayload(
    boolean success,
    int     newMoney,
    String  itemId,
    int     quantity,
    int     itemIndex
) implements CustomPacketPayload {

    public static final Type<ShopPurchaseResultPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "shop_purchase_result"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopPurchaseResultPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL,        ShopPurchaseResultPayload::success,
            ByteBufCodecs.INT,         ShopPurchaseResultPayload::newMoney,
            ByteBufCodecs.STRING_UTF8, ShopPurchaseResultPayload::itemId,
            ByteBufCodecs.INT,         ShopPurchaseResultPayload::quantity,
            ByteBufCodecs.INT,         ShopPurchaseResultPayload::itemIndex,
            ShopPurchaseResultPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopPurchaseResultPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(ShopPurchaseResultPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.screen instanceof com.stardew.craft.client.gui.ShopScreen shop) {
            shop.onPurchaseResult(payload);
        }
    }
}
