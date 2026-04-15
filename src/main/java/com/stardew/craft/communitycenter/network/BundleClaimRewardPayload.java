package com.stardew.craft.communitycenter.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.communitycenter.data.BundleDataManager;
import com.stardew.craft.communitycenter.data.BundleDefinition;
import com.stardew.craft.communitycenter.data.BundleItemResolver;
import com.stardew.craft.communitycenter.menu.BundleMenu;
import com.stardew.craft.communitycenter.state.CommunityCenterSavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record BundleClaimRewardPayload(
        int bundleId
) implements CustomPacketPayload {

    public static final Type<BundleClaimRewardPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "bundle_claim_reward")
    );

    public static final StreamCodec<ByteBuf, BundleClaimRewardPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, BundleClaimRewardPayload::bundleId,
                    BundleClaimRewardPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BundleClaimRewardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sp)) return;
            if (!(sp.containerMenu instanceof BundleMenu)) return;

            CommunityCenterSavedData data = CommunityCenterSavedData.get();
            if (!data.isRewardAvailable(payload.bundleId)) return;

            BundleDefinition def = BundleDataManager.getBundle(payload.bundleId);
            if (def == null) return;

            // Parse reward string: "O <id> <count>" or "BO <id> <count>" or "R <id> <count>"
            ItemStack reward = parseRewardString(def.rewardString());
            if (!reward.isEmpty()) {
                // Give to player (drop if inventory full)
                if (!sp.getInventory().add(reward)) {
                    sp.drop(reward, false);
                }
            }

            // Mark reward as claimed
            data.setRewardAvailable(payload.bundleId, false);

            // Sync to client
            BundleSyncPayload.sendFullSync(sp);
        });
    }

    /**
     * Parse SDV reward string format.
     * Formats: "O <sdvId> <count>", "BO <sdvId> <count>", "R <sdvId> <count>"
     * O = Object, BO = BigCraftable/Object, R = Ring
     */
    public static ItemStack parseRewardString(String rewardString) {
        if (rewardString == null || rewardString.isBlank()) return ItemStack.EMPTY;

        String[] parts = rewardString.trim().split("\\s+");
        if (parts.length < 3) return ItemStack.EMPTY;

        String type = parts[0];   // "O", "BO", or "R"
        String sdvId = parts[1];
        int count;
        try {
            count = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return ItemStack.EMPTY;
        }

        // Prefix BigCraftable / Ring IDs to avoid numeric collision with Object IDs
        String lookupKey = switch (type) {
            case "BO" -> "BO_" + sdvId;
            case "R"  -> "R_"  + sdvId;
            default   -> sdvId;          // "O" — plain Object
        };

        // Resolve SDV ID to mod item
        String modPath = BundleItemResolver.resolve(lookupKey);
        if (modPath == null) {
            // Fallback: try unprefixed key (backwards compat for R items already in map)
            modPath = BundleItemResolver.resolve(sdvId);
        }
        ItemStack stack = BundleItemResolver.resolveItemStack(modPath != null ? modPath : sdvId);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        stack.setCount(count);
        return stack;
    }
}
