package com.stardew.craft.sewer;

import com.stardew.craft.cutscene.server.EventSeenData;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.museum.MuseumDonationData;
import com.stardew.craft.network.payload.HoldUpItemPayload;
import com.stardew.craft.player.PlayerDataEventHandler;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public final class SewerService {
    private static final String RUSTY_KEY_WAKE_UP_EVENT = "gunther_rusty_key_wake_up";
    private static final int RUSTY_KEY_DONATION_THRESHOLD = 60;

    private SewerService() {
    }

    public static boolean hasRustyKey(PlayerStardewData data) {
        return data != null
            && (data.hasMailFlag(SewerStoryFlags.HAS_RUSTY_KEY)
                || data.hasSpecialItem(SewerStoryFlags.RUSTY_KEY_SPECIAL_ITEM));
    }

    public static boolean grantRustyKey(ServerPlayer player) {
        return grantRustyKey(player, true);
    }

    public static boolean grantRustyKey(ServerPlayer player, boolean notify) {
        if (player == null) {
            return false;
        }
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        boolean alreadyHadKey = hasRustyKey(data);
        if (!data.hasMailFlag(SewerStoryFlags.HAS_RUSTY_KEY)) {
            data.addMailFlag(SewerStoryFlags.HAS_RUSTY_KEY);
        }
        if (!data.hasSpecialItem(SewerStoryFlags.RUSTY_KEY_SPECIAL_ITEM)) {
            data.addSpecialItem(SewerStoryFlags.RUSTY_KEY_SPECIAL_ITEM);
        }
        PlayerDataManager.get().savePlayerData(player.getUUID(), data);
        PlayerDataEventHandler.syncPlayerData(player, data);

        if (notify && !alreadyHadKey) {
            player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
            player.sendSystemMessage(Component.translatable("stardewcraft.item.rusty_key.obtained"));
            HoldUpItemPayload.sendTo(player, new ItemStack(ModItems.RUSTY_KEY.get()));
        }
        return !alreadyHadKey;
    }

    public static void backfillRustyKeyWakeUpEligibility(ServerPlayer player) {
        if (player == null) {
            return;
        }

        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (hasRustyKey(data)) {
            return;
        }

        EventSeenData seenData = EventSeenData.get(player.serverLevel());
        if (seenData.hasSeen(player.getUUID(), SewerStoryFlags.RUSTY_KEY_EVENT_READY)
            || seenData.hasSeen(player.getUUID(), RUSTY_KEY_WAKE_UP_EVENT)) {
            return;
        }

        MuseumDonationData museumData = MuseumDonationData.get(player.serverLevel());
        if (museumData.getDonatedItems(player.getUUID()).size() < RUSTY_KEY_DONATION_THRESHOLD) {
            return;
        }

        seenData.markSeen(player.getUUID(), SewerStoryFlags.RUSTY_KEY_EVENT_READY);
    }

    public static void markOpenedSewer(ServerPlayer player) {
        if (player == null) {
            return;
        }
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        if (data.hasMailFlag(SewerStoryFlags.OPENED_SEWER)) {
            return;
        }
        data.addMailFlag(SewerStoryFlags.OPENED_SEWER);
        PlayerDataManager.get().savePlayerData(player.getUUID(), data);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
}