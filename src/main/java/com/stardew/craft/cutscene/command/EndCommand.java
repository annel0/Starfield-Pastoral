package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * Ends the event: cleans up actors, restores NPCs, unfreezes player.
 */
public class EndCommand implements EventCommand {

    private boolean done = false;

    @Override
    public void start(EventPlayer player) {
        player.endEvent();
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
