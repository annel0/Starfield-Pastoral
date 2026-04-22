package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import com.stardew.craft.cutscene.runtime.EventScreenFade;

/**
 * fade: fade to black or fade from black.
 * JSON: { "cmd": "fade", "mode": "out", "ticks": 20 }
 * Modes: "out" = fade to black, "in" = fade from black
 */
public class FadeCommand implements EventCommand {

    private final boolean fadeOut; // true = to black, false = from black
    private final int totalTicks;
    private int ticksElapsed;
    private boolean done;

    public FadeCommand(boolean fadeOut, int ticks) {
        this.fadeOut = fadeOut;
        this.totalTicks = Math.max(1, ticks);
    }

    @Override
    public void start(EventPlayer player) {
        if (fadeOut) {
            EventScreenFade.startFadeToBlack(totalTicks);
        } else {
            EventScreenFade.startFadeFromBlack(totalTicks);
        }
        ticksElapsed = 0;
        done = false;
    }

    @Override
    public void tick(EventPlayer player) {
        ticksElapsed++;
        if (ticksElapsed >= totalTicks) {
            done = true;
        }
    }

    @Override
    public boolean isComplete() { return done; }
}
