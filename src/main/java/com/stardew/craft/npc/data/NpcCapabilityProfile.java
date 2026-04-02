package com.stardew.craft.npc.data;

import java.util.Locale;

/**
 * Capability flags and personality traits for each NPC instance in data-driven runtime.
 * <p>
 * Personality constants mirror vanilla Stardew Valley NPC.cs:
 * <ul>
 *   <li>age: 0=adult, 1=teen, 2=child</li>
 *   <li>manners: 0=neutral, 1=polite, 2=rude</li>
 *   <li>socialAnxiety: 0=outgoing, 1=shy</li>
 *   <li>optimism: 0=positive, 1=negative</li>
 *   <li>gender: 0=male, 1=female</li>
 * </ul>
 */
public record NpcCapabilityProfile(
    String npcId,
    boolean implemented,
    boolean pathingEnabled,
    String animationProfile,
    int age,
    int manners,
    int socialAnxiety,
    int optimism,
    int gender,
    boolean datable
) {
    public static final String ANIM_IDLE_ONLY = "idle_only";
    public static final String ANIM_IDLE_WALK = "idle_walk";

    // Age constants
    public static final int AGE_ADULT = 0;
    public static final int AGE_TEEN = 1;
    public static final int AGE_CHILD = 2;

    // Manners constants
    public static final int MANNERS_NEUTRAL = 0;
    public static final int MANNERS_POLITE = 1;
    public static final int MANNERS_RUDE = 2;

    // Social anxiety constants
    public static final int SOCIAL_OUTGOING = 0;
    public static final int SOCIAL_SHY = 1;

    // Optimism constants
    public static final int OPTIMISM_POSITIVE = 0;
    public static final int OPTIMISM_NEGATIVE = 1;

    // Gender constants
    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;

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
