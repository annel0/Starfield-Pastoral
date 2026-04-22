package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * Unlocks player input, restores HUD.
 */
public class UnlockPlayerCommand implements EventCommand {

    private boolean done = false;

    @Override
    public void start(EventPlayer player) {
        player.setPlayerFrozen(false);
        done = true;
    }

    @Override
    public void tick(EventPlayer player) {
        // instant
    }

    @Override
    public boolean isComplete() {
        return done;
    }
}
