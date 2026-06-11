package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

public class RestoreTemporaryBlockCommand implements EventCommand {
    private final String id;
    private boolean done;

    public RestoreTemporaryBlockCommand(String id) {
        this.id = id;
    }

    @Override
    public void start(EventPlayer player) {
        TemporaryBlockCommand.restore(id);
        done = true;
    }

    @Override
    public void tick(EventPlayer player) {
    }

    @Override
    public boolean isComplete() {
        return done;
    }

    @Override
    public void onSkip(EventPlayer player) {
        TemporaryBlockCommand.restore(id);
        done = true;
    }

    @Override
    public boolean isStateCommand() {
        return true;
    }
}
