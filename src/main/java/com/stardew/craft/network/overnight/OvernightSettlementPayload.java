package com.stardew.craft.network.overnight;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

@SuppressWarnings("null")
public record OvernightSettlementPayload(List<ShippedItem> shippedItems, List<LevelUpData> levelUps) implements CustomPacketPayload {

    public static final Type<OvernightSettlementPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "overnight_settlement"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OvernightSettlementPayload> STREAM_CODEC = StreamCodec.composite(
            ShippedItem.STREAM_CODEC.apply(ByteBufCodecs.list()), OvernightSettlementPayload::shippedItems,
            LevelUpData.STREAM_CODEC.apply(ByteBufCodecs.list()), OvernightSettlementPayload::levelUps,
            OvernightSettlementPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OvernightSettlementPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OvernightSettlementPayload payload) {
        ClientOvernightHandler.startSequence(payload);
    }

    public record LevelUpData(int skillIndex, int newLevel) {
        public static final StreamCodec<RegistryFriendlyByteBuf, LevelUpData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, LevelUpData::skillIndex,
                ByteBufCodecs.VAR_INT, LevelUpData::newLevel,
                LevelUpData::new
        );
    }

    public record ShippedItem(ItemStack stack, int category, int pricePerItem) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ShippedItem> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, ShippedItem::stack,
                ByteBufCodecs.VAR_INT, ShippedItem::category,
                ByteBufCodecs.VAR_INT, ShippedItem::pricePerItem,
                ShippedItem::new
        );
    }
}
