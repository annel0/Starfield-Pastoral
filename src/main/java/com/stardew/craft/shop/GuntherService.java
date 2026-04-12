package com.stardew.craft.shop;

import com.stardew.craft.entity.npc.StardewNpcEntity;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.museum.MuseumDonationData;
import com.stardew.craft.museum.MuseumRewardRegistry;
import com.stardew.craft.network.MuseumDonationSyncPacket;
import com.stardew.craft.network.payload.OpenGuntherMenuPayload;
import com.stardew.craft.network.payload.OpenNpcDialogueScreenPayload;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Server-side handler for Gunther's museum interactions.
 * SDV parity: Gunther shows a question dialog:
 * - If donation mode is NOT active: "Donate" (if has items) / "Leave"
 * - If donation mode IS active: "End Donation" / "Leave"
 */
@SuppressWarnings("null")
public final class GuntherService {

    // Counter area: (13070,71,13057) to (13072,73,13063) — museum
    private static final int COUNTER_MIN_X = 13070;
    private static final int COUNTER_MAX_X = 13072;
    private static final int COUNTER_MIN_Y = 71;
    private static final int COUNTER_MAX_Y = 73;
    private static final int COUNTER_MIN_Z = 13057;
    private static final int COUNTER_MAX_Z = 13063;

    private GuntherService() {}

    public static boolean isPlayerAtCounter(ServerPlayer player) {
        int px = (int) Math.floor(player.getX());
        int py = (int) Math.floor(player.getY());
        int pz = (int) Math.floor(player.getZ());
        boolean result = px >= COUNTER_MIN_X && px <= COUNTER_MAX_X
            && py >= COUNTER_MIN_Y && py <= COUNTER_MAX_Y
            && pz >= COUNTER_MIN_Z && pz <= COUNTER_MAX_Z;
        System.out.println("[GuntherService] Player pos: " + px + "," + py + "," + pz
            + " | Counter: X[" + COUNTER_MIN_X + "-" + COUNTER_MAX_X
            + "] Y[" + COUNTER_MIN_Y + "-" + COUNTER_MAX_Y
            + "] Z[" + COUNTER_MIN_Z + "-" + COUNTER_MAX_Z + "] | result=" + result);
        return result;
    }

    public static InteractionResult handleGuntherInteraction(ServerPlayer player, StardewNpcEntity gunther) {
        gunther.setYRot(90f);
        gunther.setYHeadRot(90f);

        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        boolean donationActive = data.isDonationModeActive();
        boolean hasDonatable = !donationActive && playerHasDonatableItem(player);

        if (!donationActive && !hasDonatable) {
            // No donatable items and not in donation mode: show normal dialogue
            // SDV parity: "You don't have anything to donate right now."
            PacketDistributor.sendToPlayer(player, new OpenNpcDialogueScreenPayload(
                "gunther",
                "stardewcraft.npc.gunther.dialogue.nothing_to_donate",
                0
            ));
            return InteractionResult.SUCCESS;
        }

        PacketDistributor.sendToPlayer(player, new OpenGuntherMenuPayload(donationActive, hasDonatable));
        return InteractionResult.SUCCESS;
    }

    /**
     * Handle the player's choice from Gunther's question dialog.
     * 0 = Start donation mode
     * 1 = End donation mode
     */
    public static void handleChoice(ServerPlayer player, int choice) {
        switch (choice) {
            case 0 -> startDonation(player);
            case 1 -> endDonation(player);
        }
    }

    private static void startDonation(ServerPlayer player) {
        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        if (data.isDonationModeActive()) return;
        data.startDonationMode();
        syncDonations(data);
        // SDV parity: Gunther tells the player to come back when done
        PacketDistributor.sendToPlayer(player, new OpenNpcDialogueScreenPayload(
            "gunther",
            "stardewcraft.npc.gunther.donation_started",
            0
        ));
    }

    private static void endDonation(ServerPlayer player) {
        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        if (!data.isDonationModeActive()) return;
        MuseumDonationData.EndSessionResult result = data.endDonationMode();
        syncDonations(data);

        if (!result.success()) {
            PacketDistributor.sendToPlayer(player, new OpenNpcDialogueScreenPayload(
                "gunther",
                "stardewcraft.npc.gunther.donation_ended",
                0
            ));
            return;
        }

        // Check & grant museum milestone rewards
        List<MuseumRewardRegistry.MuseumReward> claimable =
            MuseumRewardRegistry.getClaimableRewards(data, data.getClaimedMuseumRewards());

        if (!claimable.isEmpty()) {
            PlayerStardewData pData = PlayerDataManager.getPlayerData(player);
            List<String> rewardNames = new ArrayList<>();
            for (MuseumRewardRegistry.MuseumReward reward : claimable) {
                // Give reward items
                for (ItemStack stack : MuseumRewardRegistry.createRewardStacks(reward)) {
                    if (!player.getInventory().add(stack.copy())) {
                        player.drop(stack, false);
                    }
                }
                // Unlock recipe if specified
                if (reward.grantRecipe() != null) {
                    pData.unlockRecipe(reward.grantRecipe());
                }
                // Mark as claimed
                data.claimReward(reward.id());

                // Build display name
                for (MuseumRewardRegistry.RewardItem ri : reward.rewardItems()) {
                    net.minecraft.world.item.Item item = MuseumRewardRegistry.resolveItem(ri.itemId());
                    if (item != null) {
                        String name = new ItemStack(item).getHoverName().getString();
                        rewardNames.add(ri.count() > 1 ? name + " x" + ri.count() : name);
                    }
                }
            }

            // Show reward dialogue listing all items received
            // Use a dynamic dialogue — we'll call sendDialogueRaw directly
            PacketDistributor.sendToPlayer(player, new OpenNpcDialogueScreenPayload(
                "gunther",
                "stardewcraft.npc.gunther.reward_granted",
                0
            ));
        } else {
            // Prismatic Shard hint check
            PlayerStardewData pData = PlayerDataManager.getPlayerData(player);
            if (!pData.hasMailFlag("galaxySword")
                    && data.getDonatedItems().contains("stardewcraft:prismatic_shard")) {
                PacketDistributor.sendToPlayer(player, new OpenNpcDialogueScreenPayload(
                    "gunther",
                    "stardewcraft.npc.gunther.prismatic_hint",
                    0
                ));
            } else {
                PacketDistributor.sendToPlayer(player, new OpenNpcDialogueScreenPayload(
                    "gunther",
                    "stardewcraft.npc.gunther.donation_ended",
                    0
                ));
            }
        }
    }

    private static void syncDonations(MuseumDonationData data) {
        List<String> ids = new ArrayList<>(data.getDonatedItems());
        PacketDistributor.sendToAllPlayers(new MuseumDonationSyncPacket(ids));
    }

    /**
     * Check if the player has any mineral/artifact items that haven't been donated yet.
     */
    public static boolean playerHasDonatableItem(ServerPlayer player) {
        MuseumDonationData data = MuseumDonationData.get(player.serverLevel());
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof IStardewItem stardewItem)) continue;
            String typeKey = stardewItem.getItemTypeKey();
            if (!"stardewcraft.type.mineral".equals(typeKey) && !"stardewcraft.type.artifact".equals(typeKey)) continue;
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (!data.isDonated(itemId)) return true;
        }
        return false;
    }
}
