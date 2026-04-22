package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

import java.util.List;

/**
 * Wraps multiple commands to execute in parallel.
 * All sub-commands start simultaneously; the group completes when all are done.
 *
 * Created by EventCommandFactory when it encounters a "simultaneous" block.
 * JSON:
 * {
 *   "cmd": "simultaneous",
 *   "commands": [
 *     {"cmd": "move_actor", "actor": "wizard", "x": 5, "y": 0, "z": 0, "ticks": 40},
 *     {"cmd": "move_player", "x": -3, "y": 0, "z": 0, "ticks": 40},
 *     {"cmd": "camera", "x": 100, "y": 70, "z": 200, "yaw": 0, "pitch": 30, "ticks": 40}
 *   ]
 * }
 */
public class SimultaneousCommand implements EventCommand {

    private final List<EventCommand> subCommands;
    private boolean started = false;

    public SimultaneousCommand(List<EventCommand> subCommands) {
        this.subCommands = subCommands;
    }

    @Override
    public void start(EventPlayer player) {
        started = true;
        for (EventCommand cmd : subCommands) {
            cmd.start(player);
        }
    }

    @Override
    public void tick(EventPlayer player) {
        for (EventCommand cmd : subCommands) {
            if (!cmd.isComplete()) {
                cmd.tick(player);
            }
        }
    }

    @Override
    public boolean isComplete() {
        if (!started) return false;
        for (EventCommand cmd : subCommands) {
            if (!cmd.isComplete()) return false;
        }
        return true;
    }

    @Override
    public void onSkip(EventPlayer player) {
        for (EventCommand cmd : subCommands) {
            cmd.onSkip(player);
        }
    }

    @Override
    public boolean isStateCommand() {
        for (EventCommand cmd : subCommands) {
            if (cmd.isStateCommand()) return true;
        }
        return false;
    }
}
