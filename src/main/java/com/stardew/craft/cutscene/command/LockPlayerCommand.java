package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * Locks player input, hides HUD. First command in most events.
 */
public class LockPlayerCommand implements EventCommand {

    private boolean done = false;

    @Override
    public void start(EventPlayer player) {
        player.setPlayerFrozen(true);
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
