package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventCameraController;
import com.stardew.craft.cutscene.runtime.EventPlayer;

public class CameraResetCommand implements EventCommand {
    @Override
    public void start(EventPlayer player) {
        EventCameraController.release();
    }
    @Override
    public void tick(EventPlayer player) {}
    @Override
    public boolean isComplete() { return true; }
}
