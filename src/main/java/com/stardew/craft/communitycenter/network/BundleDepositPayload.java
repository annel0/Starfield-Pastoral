package com.stardew.craft.communitycenter.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.communitycenter.menu.BundleMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * C→S: Client requests depositing the carried (cursor) item
 * into a bundle ingredient slot.
 */
@SuppressWarnings("null")
public record BundleDepositPayload(
        int bundleId,
        int ingredientSlotIndex
) implements CustomPacketPayload {

    public static final Type<BundleDepositPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "bundle_deposit")
    );

    public static final StreamCodec<ByteBuf, BundleDepositPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, BundleDepositPayload::bundleId,
                    ByteBufCodecs.VAR_INT, BundleDepositPayload::ingredientSlotIndex,
                    BundleDepositPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BundleDepositPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sp)) return;
            if (!(sp.containerMenu instanceof BundleMenu menu)) return;

            // Get the carried (cursor) item — this is the item the player picked up by clicking
            ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) return;

            boolean success = menu.tryDeposit(sp, payload.bundleId, payload.ingredientSlotIndex, carried);
            if (success) {
                // tryDeposit already called carried.shrink(); MC syncs carried automatically
                sp.containerMenu.broadcastChanges();

                // Send sync to client
                BundleSyncPayload.sendFullSync(sp);
            }
        });
    }
}
