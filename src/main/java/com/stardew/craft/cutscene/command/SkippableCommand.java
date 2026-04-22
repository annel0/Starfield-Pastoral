package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * Marks the event as skippable from this point onward.
 */
public class SkippableCommand implements EventCommand {

    private boolean done = false;

    @Override
    public void start(EventPlayer player) {
        player.setSkippable(true);
        done = true;
    }

    @Override
    public void tick(EventPlayer player) {}

    @Override
    public boolean isComplete() {
        return done;
    }
}
