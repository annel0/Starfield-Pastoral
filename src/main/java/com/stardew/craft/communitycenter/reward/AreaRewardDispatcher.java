package com.stardew.craft.communitycenter.reward;

import com.stardew.craft.communitycenter.reward.panning.FishTankReward;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Dispatches per-area completion rewards for a specific player after the
 * corresponding CC area is marked complete.
 * <p>
 * SDV parity: {@code CommunityCenter.answerLetter*} / area-specific mail triggers
 * (see StardewValley.Locations/CommunityCenter.cs:737–745).
 * <p>
 * Currently routes area 2 (Fish Tank) and area 5 (Bulletin) here — the other
 * three already-shipped rewards (Greenhouse, Quarry bridge, Minecarts) keep
 * their existing hook sites in {@code BundleMenu.onAreaComplete}.
 */
public final class AreaRewardDispatcher {

    private AreaRewardDispatcher() {}

    public static void onAreaComplete(ServerPlayer player, int areaId, ServerLevel level) {
        switch (areaId) {
            case 2 -> FishTankReward.apply(player, level);
            case 5 -> BulletinReward.onAreaComplete(player);
            default -> { /* 0/1/3/4: existing hooks or not implemented */ }
        }
    }
}
