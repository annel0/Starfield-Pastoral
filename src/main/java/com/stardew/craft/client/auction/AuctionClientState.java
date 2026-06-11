package com.stardew.craft.client.auction;

import com.stardew.craft.network.payload.SyncAuctionBoardPayload;
import net.minecraft.world.item.ItemStack;

public final class AuctionClientState {
    private static SyncAuctionBoardPayload board = SyncAuctionBoardPayload.clear();
    private static long updatedAtMs;

    private AuctionClientState() {
    }

    public static void update(SyncAuctionBoardPayload payload) {
        board = payload == null ? SyncAuctionBoardPayload.clear() : payload;
        updatedAtMs = System.currentTimeMillis();
    }

    public static SyncAuctionBoardPayload board() {
        if (!board.active() || board.stack() == null || board.stack().isEmpty()) {
            return SyncAuctionBoardPayload.clear();
        }
        if (System.currentTimeMillis() - updatedAtMs > 12_000L) {
            return SyncAuctionBoardPayload.clear();
        }
        return board;
    }

    public static ItemStack stack() {
        return board().stack();
    }

    /**
     * Server syncs the lot every ~2s; interpolate the countdown locally so the timer ticks down
     * smoothly each frame instead of jumping between syncs.
     */
    public static int liveRemainingSeconds() {
        SyncAuctionBoardPayload current = board();
        if (!current.active()) {
            return 0;
        }
        long elapsed = Math.max(0L, System.currentTimeMillis() - updatedAtMs) / 1000L;
        return Math.max(0, current.remainingSeconds() - (int) elapsed);
    }
}
