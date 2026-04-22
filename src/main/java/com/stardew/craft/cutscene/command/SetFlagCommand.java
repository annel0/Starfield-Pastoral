package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.network.CutsceneServerActionPayload;
import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * set_flag: set a mail flag on the player (server-side).
 * JSON: {"cmd":"set_flag", "flag":"seenJunimoNote"}
 */
public class SetFlagCommand implements EventCommand {
    private final String flag;

    public SetFlagCommand(String flag) {
        this.flag = flag;
    }

    @Override
    public void start(EventPlayer player) {
        PacketDistributor.sendToServer(
            new CutsceneServerActionPayload("set_flag", flag));
    }

    @Override public void tick(EventPlayer player) {}
    @Override public boolean isComplete() { return true; }
}
