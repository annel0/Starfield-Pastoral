package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.animal.service.AnimalShopService;
import com.stardew.craft.animal.service.AnimalAcquireService;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AnimalPurchaseSubmitPayload(String animalTypeId, String buildingId, String customName) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<AnimalPurchaseSubmitPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animal_purchase_submit"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, AnimalPurchaseSubmitPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.animalTypeId, 64);
            buf.writeUtf(payload.buildingId, 128);
            buf.writeUtf(payload.customName, 128);
        },
        buf -> new AnimalPurchaseSubmitPayload(buf.readUtf(64), buf.readUtf(128), buf.readUtf(128))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(AnimalPurchaseSubmitPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            AnimalShopService.ShopAnimalRule rule = AnimalShopService.getRule(payload.animalTypeId);
            int price = rule == null ? -1 : rule.price();
            if (price <= 0) {
                return;
            }
            if (PlayerStardewDataAPI.getMoney(serverPlayer) < price) {
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.no_money"));
                return;
            }

            String normalizedName = payload.customName == null ? "" : payload.customName.trim();
            AnimalWorldData data = AnimalWorldData.get(serverPlayer.serverLevel());
            var buildingOpt = data.getBuilding(payload.buildingId);
            if (buildingOpt.isEmpty()) {
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.failed"));
                return;
            }

            String ownerUuid = serverPlayer.getUUID().toString();
            if (!ownerUuid.equals(buildingOpt.get().ownerPlayerUuid())) {
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.failed"));
                return;
            }

            int ownerTier = AnimalShopService.getOwnerMaxTier(data, ownerUuid, rule.family());
            if (ownerTier < rule.requiredTier()) {
                serverPlayer.sendSystemMessage(Component.translatable(rule.lockReasonKey()));
                return;
            }

            if (!AnimalShopService.canPurchaseInBuilding(rule, buildingOpt.get())) {
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.no_building"));
                return;
            }

            String finalName = normalizedName.isBlank() ? rule.defaultName() : normalizedName;
            if (data.hasAnyAnimalWithName(finalName)) {
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.name_duplicate"));
                return;
            }

            try {
                var record = AnimalAcquireService.purchase(serverPlayer.serverLevel(), payload.animalTypeId, finalName, payload.buildingId);
                if (!PlayerStardewDataAPI.removeMoney(serverPlayer, price)) {
                    return;
                }
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.success", record.customName(), price));
            } catch (IllegalArgumentException | IllegalStateException ex) {
                serverPlayer.sendSystemMessage(Component.translatable("stardewcraft.animal.purchase.failed"));
            }
        });
    }
}
