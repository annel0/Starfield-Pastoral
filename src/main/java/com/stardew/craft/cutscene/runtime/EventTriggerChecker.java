package com.stardew.craft.cutscene.runtime;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.cutscene.data.EventData;
import com.stardew.craft.cutscene.data.EventRegistry;
import com.stardew.craft.cutscene.data.EventTrigger;
import com.stardew.craft.cutscene.network.ClientEventSeenCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

/**
 * Client-side trigger checker that runs every N ticks.
 * Checks enter_area events by matching the player position against event AABB areas.
 *
 * Hook: called from ModClientEvents.onClientTick() after EventPlayer.tick().
 */
@OnlyIn(Dist.CLIENT)
public final class EventTriggerChecker {

    private static final int CHECK_INTERVAL = 4; // every ~0.2 seconds
    private static int tickCounter = 0;

    /** Cooldown after an event ends before checking again (prevents re-trigger). */
    private static int cooldownTicks = 0;
    private static final int POST_EVENT_COOLDOWN = 40; // 2 seconds

    /**
     * Grace period after joining a world / changing dimension before any
     * auto-trigger is allowed. This prevents the runtime from firing a
     * cutscene before the seen-cache and player-data caches have arrived
     * from the server, and before the player's chunks are fully loaded.
     */
    private static final int JOIN_GRACE_TICKS = 4; // ~0.2 seconds
    private static int joinGraceTicks = JOIN_GRACE_TICKS;
    private static ResourceKey<Level> lastDimension = null;

    /** Maps event trigger location names to their dimension keys. */
    private static final Map<String, ResourceKey<Level>> LOCATION_TO_DIMENSION = Map.of(
            "beach", ModDimensions.STARDEW_VALLEY,
            "town", ModDimensions.STARDEW_VALLEY,
            "farm", ModDimensions.STARDEW_VALLEY,
            "forest", ModDimensions.STARDEW_VALLEY,
            "mountain", ModDimensions.STARDEW_VALLEY,
            "mine", ModMiningDimensions.STARDEW_MINING
    );

    /**
     * Locations whose dimension is dedicated entirely to that location.
     * For these, an enter_area event without AABB triggers anywhere
     * in the matching dimension (the dimension itself = the location).
     * Locations NOT listed here share STARDEW_VALLEY with other locations
     * (beach/town/farm/forest/mountain) and therefore REQUIRE an explicit
     * AABB to disambiguate which sub-region the player is in.
     */
    private static final java.util.Set<String> WHOLE_DIMENSION_LOCATIONS = java.util.Set.of(
            "mine"
    );

    private EventTriggerChecker() {}

    /**
     * Called every client tick from ModClientEvents.
     */
    public static void tick() {
        // Don't check during cutscene playback
        if (EventPlayer.get().isRunning()) {
            cooldownTicks = POST_EVENT_COOLDOWN;
            return;
        }

        // Post-event cooldown
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Only check in Stardew-related dimensions
        @SuppressWarnings("null")
        ResourceKey<Level> currentDim = mc.level.dimension();
        if (currentDim != ModDimensions.STARDEW_VALLEY
                && currentDim != ModMiningDimensions.STARDEW_MINING) {
            // Reset grace whenever we leave Stardew dimensions so re-entry
            // gets the same protection.
            lastDimension = currentDim;
            joinGraceTicks = JOIN_GRACE_TICKS;
            return;
        }

        // Reset grace on dimension change (login also goes through here
        // since lastDimension starts null).
        if (lastDimension != currentDim) {
            lastDimension = currentDim;
            joinGraceTicks = JOIN_GRACE_TICKS;
        }

        if (joinGraceTicks > 0) {
            joinGraceTicks--;
            return;
        }

        // Wait for every client cache that any precondition could read. If any of these
        // is stale (pre-login defaults / previous world's data), negative preconditions
        // like "not_mail" or "not_saw_event" would trivially pass and the event would
        // fire — then get marked seen — before the player's real state arrived. Positive
        // preconditions would also spuriously fail, letting an event fire after the player
        // has already walked past the trigger area.
        if (!ClientEventSeenCache.isSynced()) return;
        if (!com.stardew.craft.client.ClientPlayerDataCache.isSynced()) return;
        if (!com.stardew.craft.client.NpcFriendshipClientCache.isSynced()) return;
        if (!com.stardew.craft.weather.ClientWeatherCache.isSynced()) return;
        if (!com.stardew.craft.client.hud.StardewTimeHud.isTimeSynced()) return;

        // Require the chunk under the player to be loaded — auto triggers
        // should never run while the world around the player is still
        // streaming in.
        LocalPlayer localPlayer = mc.player;
        net.minecraft.client.multiplayer.ClientLevel localLevel = mc.level;
        if (localPlayer == null || localLevel == null) return;
        net.minecraft.core.BlockPos playerBlock = localPlayer.blockPosition();
        if (!localLevel.isLoaded(playerBlock)) return;

        checkEnterAreaEvents(localPlayer, currentDim);
    }

    private static void checkEnterAreaEvents(LocalPlayer player, ResourceKey<Level> currentDim) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        for (EventData event : EventRegistry.all()) {
            EventTrigger trigger = event.trigger();
            if (!"enter_area".equals(trigger.type())) continue;

            String location = trigger.location();
            if (location == null) continue; // ill-formed event, skip

            // 1) Dimension gate: the player's current dimension must match the
            //    dimension that this location lives in. Unknown locations are
            //    rejected to avoid accidental dimension-wide triggers.
            ResourceKey<Level> expectedDim = LOCATION_TO_DIMENSION.get(location);
            if (expectedDim == null || expectedDim != currentDim) continue;

            // 2) Sub-region gate:
            //    - If the dimension is shared by multiple locations
            //      (e.g. STARDEW_VALLEY hosts beach/town/farm/...),
            //      the event MUST define an explicit AABB.
            //    - If the location occupies its entire dimension (e.g. "mine"),
            //      AABB is optional; missing AABB means "anywhere in the dim".
            double[] min = trigger.areaMin();
            double[] max = trigger.areaMax();
            boolean wholeDim = WHOLE_DIMENSION_LOCATIONS.contains(location);
            if (min == null || max == null) {
                if (!wholeDim) continue; // sub-area location must specify AABB
            } else {
                if (min.length < 3 || max.length < 3) continue;
                if (px < min[0] || px > max[0]) continue;
                if (py < min[1] || py > max[1]) continue;
                if (pz < min[2] || pz > max[2]) continue;
            }

            // 3) Per-event seen check (server-synced)
            if (ClientEventSeenCache.hasSeen(event.id())) continue;

            // 4) Preconditions
            if (!PreconditionEvaluator.evaluate(event.preconditions())) continue;

            // Trigger!
            EventPlayer.get().start(event);
            return; // only one event at a time
        }
    }

    /**
     * Check if any interact_npc events are pending for a given NPC.
     * Called from server-side NpcInteractionService before opening dialogue.
     * Returns the event ID to trigger, or null if none.
     */
    public static String findPendingNpcEvent(String npcId) {
        List<EventData> events = EventRegistry.getByNpc(npcId);
        if (events.isEmpty()) return null;

        for (EventData event : events) {
            if (ClientEventSeenCache.hasSeen(event.id())) continue;
            if (!PreconditionEvaluator.evaluate(event.preconditions())) continue;
            return event.id();
        }
        return null;
    }

    /**
     * Reset state (e.g. on disconnect).
     */
    public static void reset() {
        tickCounter = 0;
        cooldownTicks = 0;
        joinGraceTicks = JOIN_GRACE_TICKS;
        lastDimension = null;
    }
}
