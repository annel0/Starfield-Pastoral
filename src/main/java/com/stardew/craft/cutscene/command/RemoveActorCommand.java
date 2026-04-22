package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * remove_actor: despawns a previously spawned actor.
 * JSON: { "cmd": "remove_actor", "actor": "alice" }
 */
public class RemoveActorCommand implements EventCommand {

    private final String actorTag;

    public RemoveActorCommand(String actorTag) {
        this.actorTag = actorTag;
    }

    @Override
    public void start(EventPlayer player) {
        player.removeActor(actorTag);
    }

    @Override
    public void tick(EventPlayer player) {}

    @Override
    public boolean isComplete() { return true; }
}
