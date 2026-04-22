package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import com.stardew.craft.cutscene.runtime.EventScreenFade;

/**
 * screen_flash: quick white flash effect (fast fade-out then fade-in).
 * JSON: {"cmd":"screen_flash"}
 * Optional: "ticks": 4 (total flash duration, default 4)
 */
public class ScreenFlashCommand implements EventCommand {
    private final int totalTicks;
    private int elapsed;
    private boolean done;

    public ScreenFlashCommand(int ticks) {
        this.totalTicks = Math.max(2, ticks);
    }

    @Override
    public void start(EventPlayer player) {
        // Quick fade to white then back
        EventScreenFade.startFadeToBlack(totalTicks / 2);
        elapsed = 0;
        done = false;
    }

    @Override
    public void tick(EventPlayer player) {
        elapsed++;
        if (elapsed == totalTicks / 2) {
            EventScreenFade.startFadeFromBlack(totalTicks - totalTicks / 2);
        }
        if (elapsed >= totalTicks) {
            done = true;
        }
    }

    @Override
    public boolean isComplete() { return done; }
}
