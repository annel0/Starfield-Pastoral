package com.stardew.craft.communitycenter.reward.panning;

import com.stardew.craft.mail.MailService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fish Tank (area 2) reward — unlocks ore panning for this player.
 * <p>
 * SDV parity:
 * <ul>
 *   <li>Sets the {@code ccFishTank} mail flag (already done by BundleMenu).</li>
 *   <li>Schedules the {@code ccFishTankPan} letter for tomorrow — a thank-you
 *       note from Clint with the Copper Pan attached. In SDV the pan is given
 *       through a Linus cutscene; we simplify to a mail attachment so the
 *       player definitely gets the tool regardless of location.</li>
 *   <li>Once {@code ccFishTank} is set, {@link OrePanPointManager} will start
 *       generating ore-pan points for this player during the 10-minute tick.
 *       If the pan is lost, the player can buy a replacement from Clint
 *       (gated by the {@code ccFishTank} flag).</li>
 * </ul>
 */
public final class FishTankReward {

    private static final String MAIL_PAN_GIFT = "ccFishTankPan";

    private FishTankReward() {}

    public static void apply(ServerPlayer player, ServerLevel level) {
        MailService.addMailForTomorrow(player, MAIL_PAN_GIFT);

        com.stardew.craft.StardewCraft.LOGGER.info(
            "[CC-REWARD] Fish Tank reward applied to {}: panning unlocked, Copper Pan scheduled in mail",
            player.getName().getString());
    }
}
