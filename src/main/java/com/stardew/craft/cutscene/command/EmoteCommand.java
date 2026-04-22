package com.stardew.craft.cutscene.command;

import com.stardew.craft.client.emote.EmoteBubbleClientState;
import com.stardew.craft.cutscene.runtime.EventPlayer;
import com.stardew.craft.emote.EmoteCatalog;
import com.stardew.craft.emote.EmoteType;
import net.minecraft.world.entity.Mob;

/**
 * emote: show an emote bubble above an actor.
 * JSON: {"cmd":"emote", "actor":"robin", "emote":"happy"}
 */
public class EmoteCommand implements EventCommand {
    private final String actorTag;
    private final String emoteId;

    public EmoteCommand(String actorTag, String emoteId) {
        this.actorTag = actorTag;
        this.emoteId = emoteId;
    }

    @Override
    public void start(EventPlayer player) {
        Mob actor = player.getActor(actorTag);
        if (actor == null) return;
        EmoteType type = EmoteCatalog.byId(emoteId);
        int baseIndex = EmoteCatalog.getBubbleBaseIndex(type);
        EmoteBubbleClientState.trigger(actor.getId(), baseIndex);
    }

    @Override public void tick(EventPlayer player) {}
    @Override public boolean isComplete() { return true; }
}
