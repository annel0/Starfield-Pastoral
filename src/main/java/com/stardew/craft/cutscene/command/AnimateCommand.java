package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventActorEntity;
import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.world.entity.Mob;

/**
 * animate: play a GeckoLib animation on an actor.
 * JSON: { "cmd": "animate", "actor": "alice", "anim": "laugh", "loop": false }
 */
public class AnimateCommand implements EventCommand {

    private final String actorTag;
    private final String animName;
    private final boolean loop;

    public AnimateCommand(String actorTag, String animName, boolean loop) {
        this.actorTag = actorTag;
        this.animName = animName;
        this.loop = loop;
    }

    @Override
    public void start(EventPlayer player) {
        Mob actor = player.getActor(actorTag);
        if (actor instanceof EventActorEntity npcActor) {
            npcActor.setCustomAnimation(animName, loop);
        }
    }

    @Override
    public void tick(EventPlayer player) {}

    @Override
    public boolean isComplete() { return true; }
}
