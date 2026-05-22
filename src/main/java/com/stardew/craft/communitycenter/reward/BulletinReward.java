package com.stardew.craft.communitycenter.reward;

import com.stardew.craft.mail.MailService;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.runtime.NpcFriendshipDataManager;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;

/**
 * Bulletin Board (area 5) reward — all-village friendship boost.
 * <p>
 * SDV parity (CommunityCenter.cs:744 + LetterViewerMenu.cs:231–242):
 * <ol>
 *   <li>Area complete → schedule mail "ccBulletinThankYou" for next day.</li>
 *   <li>When the player <em>opens</em> the letter → +500 friendship to every
 *       non-datable villager; set flag "ccBulletinThankYouReceived" so the
 *       boost only applies once.</li>
 * </ol>
 */
public final class BulletinReward {

    /** SDV: Game1.player.changeFriendship(500, n) */
    public static final int FRIENDSHIP_BOOST = 500;
    /** Non-datable villager heart cap (10 hearts × 250) — matches NpcInteractionService. */
    public static final int MAX_POINTS_NON_DATABLE = 2500;

    private static final String MAIL_THANK_YOU = "ccBulletinThankYou";
    private static final String FLAG_THANK_YOU_RECEIVED = "ccBulletinThankYouReceived";

    private BulletinReward() {}

    /**
     * Called by the dispatcher the moment area 5 is marked complete.
     * SDV: CommunityCenter.cs:744 — addMailForTomorrow("ccBulletinThankYou").
     */
    public static void onAreaComplete(ServerPlayer player) {
        MailService.addMailForTomorrow(player, MAIL_THANK_YOU);
    }

    /**
     * Called by {@link MailService#openNextMail(ServerPlayer)} when the player
     * opens the {@code ccBulletinThankYou} letter.
     * <p>
     * SDV: LetterViewerMenu.cs:231 — ForEachVillager(n => if (!n.datable) changeFriendship(500, n));
     */
    public static void onThankYouLetterOpened(ServerPlayer player, ServerLevel level) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        // SDV guard: only trigger once per save (ccBulletinThankYouReceived)
        if (data.hasMailFlag(FLAG_THANK_YOU_RECEIVED)) return;

        NpcFriendshipDataManager mgr = NpcFriendshipDataManager.get(level);
        UUID uuid = player.getUUID();

        int boosted = 0;
        for (Map.Entry<String, NpcCapabilityProfile> entry : NpcDataRegistry.capabilities().entrySet()) {
            NpcCapabilityProfile profile = entry.getValue();
            if (profile == null) continue;
            // SDV: skip datable NPCs (eligible bachelors/bachelorettes handled separately)
            if (profile.datable()) continue;

            String npcId = entry.getKey();
            int amount = com.stardew.craft.book.BookPowerEffects.applyFriendshipGain(data, FRIENDSHIP_BOOST);
            mgr.getOrCreate(uuid, npcId).addPoints(amount, MAX_POINTS_NON_DATABLE);
            boosted++;
        }
        mgr.setDirty();

        data.addMailFlag(FLAG_THANK_YOU_RECEIVED);
        PlayerDataManager.get().setDirty();

        com.stardew.craft.StardewCraft.LOGGER.info(
            "[CC-REWARD] Bulletin thank-you opened by {}: +{} friendship to {} non-datable villagers",
            player.getName().getString(), FRIENDSHIP_BOOST, boosted);
    }

    public static boolean isBulletinThankYouMail(String mailId) {
        return MAIL_THANK_YOU.equals(mailId);
    }
}
