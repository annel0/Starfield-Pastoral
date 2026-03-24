package com.stardew.craft.npc.data;

import java.util.Locale;

/**
 * Capability flags for each NPC instance in data-driven runtime.
 */
public record NpcCapabilityProfile(
    String npcId,
    boolean implemented,
    boolean pathingEnabled,
    String animationProfile
) {
    public static final String ANIM_IDLE_ONLY = "idle_only";
    public static final String ANIM_IDLE_WALK = "idle_walk";

    public NpcCapabilityProfile {
        npcId = npcId == null ? "" : npcId.trim().toLowerCase(Locale.ROOT);
        animationProfile = animationProfile == null || animationProfile.isBlank()
            ? ANIM_IDLE_WALK
            : animationProfile.trim().toLowerCase(Locale.ROOT);
    }

    public boolean canRunPathing() {
        return implemented && pathingEnabled && !ANIM_IDLE_ONLY.equalsIgnoreCase(animationProfile);
    }
}
