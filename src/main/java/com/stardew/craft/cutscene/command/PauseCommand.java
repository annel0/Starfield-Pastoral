package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * Waits for a specified number of ticks before advancing.
 */
public class PauseCommand implements EventCommand {

    private final int duration;
    private int elapsed = 0;

    public PauseCommand(int ticks) {
        this.duration = ticks;
    }

    @Override
    public void tick(EventPlayer player) {
        elapsed++;
    }

    @Override
    public boolean isComplete() {
        return elapsed >= duration;
    }
}
