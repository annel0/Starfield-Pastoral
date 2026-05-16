package com.stardew.craft.cutscene.runtime;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.stardew.craft.cutscene.command.EventCommand;
import com.stardew.craft.cutscene.command.EventCommandFactory;
import com.stardew.craft.cutscene.data.EventData;
import com.stardew.craft.cutscene.network.MarkEventSeenPayload;
import com.stardew.craft.client.sound.StardewMusicManager;
import com.stardew.craft.network.payload.ClientNpcVisibilityState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.*;

/**
 * Client-side event playback engine.
 * Drives a tick-based state machine that executes event commands sequentially.
 *
 * Singleton — only one event can play at a time.
 */
@OnlyIn(Dist.CLIENT)
public final class EventPlayer {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final EventPlayer INSTANCE = new EventPlayer();

    // ─── state ───
    private EventData currentEvent = null;
    private List<EventCommand> commands = List.of();
    private int commandIndex = -1;
    private boolean running = false;
    private boolean skippable = false;
    private boolean playerFrozen = false;

    /** Dimension the cutscene was started in; if the player leaves it, abort. */
    private net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> startDimension = null;

    // Actor and NPC management
    private final Map<String, Mob> actors = new HashMap<>();
    private final Set<String> hiddenNpcs = new HashSet<>();

    private EventPlayer() {}

    public static EventPlayer get() {
        return INSTANCE;
    }

    // ─── public API ───

    /**
     * Start playing an event. Call from client thread only.
     */
    public void start(EventData event) {
        if (running) {
            LOGGER.warn("Tried to start event {} while {} is playing", event.id(), currentEvent.id());
            return;
        }

        LOGGER.info("Starting cutscene event: {}", event.id());
        currentEvent = event;
        skippable = event.skippable();

        if (event.trigger() != null && "wake_up".equals(event.trigger().type())
                && CutsceneAnchorRegistry.get("farm_spawn") == null) {
            Minecraft mc = Minecraft.getInstance();
            var localPlayer = mc.player;
            if (localPlayer != null) {
                CutsceneAnchorRegistry.set("farm_spawn", localPlayer.getX(), localPlayer.getY(), localPlayer.getZ());
                LOGGER.warn("Wake-up cutscene {} started without farm_spawn anchor; using current player position as fallback", event.id());
            }
        }

        // NOTE: Do NOT clear the anchor registry here.
        // Server-pushed anchors (e.g. farm_spawn for wake_up events) arrive BEFORE
        // the TriggerEventPayload, so wiping at start would discard them. Anchors
        // are cleared at event end / abort instead (see finish() / abort()).

        // Parse commands
        List<EventCommand> parsed = new ArrayList<>();
        for (JsonObject obj : event.rawCommands()) {
            try {
                EventCommand cmd = EventCommandFactory.create(obj);
                if (cmd != null) {
                    parsed.add(cmd);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse cutscene command: {} — {}", obj, e.getMessage());
            }
        }
        commands = parsed;
        commandIndex = 0;
        running = true;

        // Capture starting dimension so we can abort if the player teleports out.
        net.minecraft.client.multiplayer.ClientLevel cap = Minecraft.getInstance().level;
        if (cap != null) {
            startDimension = cap.dimension();
        } else {
            startDimension = null;
        }

        // Notify server so it can lock block-break / placement / entity interactions for this player
        try {
            PacketDistributor.sendToServer(
                new com.stardew.craft.cutscene.network.NotifyCutsceneStartPayload(event.id()));
        } catch (Exception e) {
            LOGGER.warn("Failed to notify server of cutscene start: {}", e.getMessage());
        }

        // Hide all GUI (like spectator)
        Minecraft.getInstance().options.hideGui = true;

        // Start first command
        if (!commands.isEmpty()) {
            commands.get(0).start(this);
        }
    }

    /**
     * Called every client tick from ModClientEvents.
     */
    public void tick() {
        if (!running || commands.isEmpty()) return;

        // Abort if the player has changed dimension since the cutscene started.
        // Otherwise camera/actor commands keep playing in a world the player is
        // no longer in (e.g. after a portal teleport mid-cutscene).
        if (startDimension != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.level.dimension() != startDimension) {
                LOGGER.warn("Cutscene {} aborted: player left starting dimension {}",
                        currentEvent != null ? currentEvent.id() : "?", startDimension);
                endEvent();
                return;
            }
        }

        if (commandIndex >= commands.size()) {
            // All commands exhausted without explicit 'end'
            endEvent();
            return;
        }

        EventCommand current = commands.get(commandIndex);
        current.tick(this);

        if (current.isComplete()) {
            commandIndex++;
            if (commandIndex < commands.size()) {
                commands.get(commandIndex).start(this);
            }
        }
    }

    /**
     * Try to skip the current event (called on ESC press).
     */
    public void trySkip() {
        if (!running || !skippable) return;

        LOGGER.info("Skipping event: {}", currentEvent.id());

        // Execute all remaining state commands
        for (int i = commandIndex; i < commands.size(); i++) {
            EventCommand cmd = commands.get(i);
            if (cmd.isStateCommand()) {
                cmd.onSkip(this);
            }
        }

        endEvent();
    }

    /**
     * Called by EndCommand or internally to finish the event.
     */
    public void endEvent() {
        if (!running) return;

        String eventId = currentEvent.id();
        LOGGER.info("Ending cutscene event: {}", eventId);

        // Restore player
        setPlayerFrozen(false);

        // Restore GUI
        Minecraft.getInstance().options.hideGui = false;

        // Remove all actors
        for (Mob actor : actors.values()) {
            actor.discard();
        }
        actors.clear();

        // Restore hidden NPCs
        for (String npcId : hiddenNpcs) {
            ClientNpcVisibilityState.show(npcId);
        }
        hiddenNpcs.clear();

        // Release camera
        EventCameraController.release();

        // Clear fade
        EventScreenFade.clear();

        StardewMusicManager.releaseCutsceneOverride();

        // Mark as seen on server if the client is still connected.
        // Disconnect-time aborts can reach here after Minecraft has already cleared
        // its connection; PacketDistributor.sendToServer requires a live connection.
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            PacketDistributor.sendToServer(new MarkEventSeenPayload(eventId));
        } else {
            LOGGER.debug("Skipped server cutscene completion for {} because the client is disconnected", eventId);
        }

        // Clean up
        currentEvent = null;
        commands = List.of();
        commandIndex = -1;
        running = false;
        skippable = false;
        startDimension = null;
        CutsceneAnchorRegistry.clear();
    }

    // ─── state accessors used by commands ───

    public boolean isRunning() {
        return running;
    }

    public EventData currentEvent() {
        return currentEvent;
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    public boolean isPlayerFrozen() {
        return playerFrozen;
    }

    public void setPlayerFrozen(boolean frozen) {
        this.playerFrozen = frozen;
        // Actual movement suppression is handled in ModClientEvents.onClientTick
    }

    // ─── Actor management ───

    public void registerActor(String tag, Mob actor) {
        actors.put(tag, actor);
    }

    public Mob getActor(String tag) {
        return actors.get(tag);
    }

    public void removeActor(String tag) {
        Mob actor = actors.remove(tag);
        if (actor != null) {
            actor.discard();
        }
    }

    // ─── NPC visibility management ───

    public void trackHiddenNpc(String npcId) {
        hiddenNpcs.add(npcId);
    }

    /**
     * Reset all state (e.g. on disconnect).
     */
    public void reset() {
        if (running) {
            setPlayerFrozen(false);
            Minecraft.getInstance().options.hideGui = false;
            for (Mob actor : actors.values()) {
                actor.discard();
            }
            actors.clear();
            for (String npcId : hiddenNpcs) {
                ClientNpcVisibilityState.show(npcId);
            }
            hiddenNpcs.clear();
            EventCameraController.release();
            EventScreenFade.clear();
            StardewMusicManager.releaseCutsceneOverride();
        }
        currentEvent = null;
        commands = List.of();
        commandIndex = -1;
        running = false;
        skippable = false;
        playerFrozen = false;
        startDimension = null;
        CutsceneAnchorRegistry.clear();
    }
}
