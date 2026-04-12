package com.stardew.craft.client.sound;

import com.stardew.craft.client.hud.MiningFloorHud;
import com.stardew.craft.client.hud.StardewTimeHud;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.sound.ModSounds;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.weather.ClientWeatherCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Client-side music manager that replicates Stardew Valley's music system.
 * <p>
 * Selects music based on: dimension, season, weather, time-of-day, mine floor.
 * Suppresses vanilla MC MusicManager when in Stardew dimensions.
 */
@SuppressWarnings("null")
public final class StardewMusicManager {

    private static final Logger LOG = LoggerFactory.getLogger(StardewMusicManager.class);

    /** How often (in ticks) to re-evaluate which track should play. */
    private static final int CHECK_INTERVAL = 40; // 2 seconds

    /** SDV: nightTime starts around 18:00 (1080 in SDV time units). */
    private static final int NIGHT_TIME = 1080;

    /** SDV: EarthMine = floors 1-39, FrostMine = floors 40-79. */
    private static final int FROST_MINE_START = 40;

    /** Interior region boundaries (client-side mirror of InteriorSubspaceManager). */
    private static final int INTERIOR_MIN = 10001;
    private static final int INTERIOR_MAX = 19000;

    /**
     * Known interior locations with their origin coordinates and associated music.
     * Null music means "fall through to outdoor seasonal music".
     */
    private record InteriorEntry(String id, int x, int z, @Nullable DeferredHolder<SoundEvent, SoundEvent> music) {}

    private static final List<InteriorEntry> INTERIORS = List.of(
        new InteriorEntry("saloon",           14208, 14208, ModSounds.MUSIC_SALOON),
        new InteriorEntry("wizard_tower",     18240, 17088, ModSounds.MUSIC_WIZARD_TOWER),
        new InteriorEntry("museum",           13056, 13056, ModSounds.MUSIC_LIBRARY),
        new InteriorEntry("adventurer_guild", 17664, 17088, ModSounds.MUSIC_ADVENTURER_GUILD),
        new InteriorEntry("clinic",           15360, 15360, ModSounds.MUSIC_HOSPITAL),
        new InteriorEntry("elliott_cabin",    17664, 18240, ModSounds.MUSIC_ELLIOTT_PIANO),
        new InteriorEntry("pierre_house",     12032, 12032, ModSounds.MUSIC_SPRINGTOWN),
        new InteriorEntry("fish_shop",        17664, 17664, null),
        new InteriorEntry("marnie_ranch",     17088, 18240, ModSounds.MUSIC_MARNIE_SHOP),
        new InteriorEntry("carpenter_shop",   16512, 16512, ModSounds.MUSIC_MARNIE_SHOP),
        // Residential — use springtown (general town/indoor music)
        new InteriorEntry("blacksmith",       13632, 13632, ModSounds.MUSIC_SPRINGTOWN),
        new InteriorEntry("mayor_house",      14784, 14784, ModSounds.MUSIC_SPRINGTOWN),
        new InteriorEntry("1_river_road",     15936, 15936, ModSounds.MUSIC_SPRINGTOWN),
        new InteriorEntry("1_willow_lane",    17088, 17088, ModSounds.MUSIC_SPRINGTOWN),
        new InteriorEntry("2_willow_lane",    17088, 17664, ModSounds.MUSIC_SPRINGTOWN),
        new InteriorEntry("leah_cottage",     17088, 18816, ModSounds.MUSIC_SPRINGTOWN)
    );

    private static int tickCounter = CHECK_INTERVAL - 1; // evaluate on first tick

    /** The currently-playing music instance, or null. */
    @Nullable
    private static StardewMusicInstance currentMusic;

    /** The SoundEvent key of the currently-playing track, for dedup. */
    @Nullable
    private static SoundEvent currentTrackEvent;

    /** Fade-out instance (old track being replaced). */
    @Nullable
    private static StardewMusicInstance fadingOut;
    private static int fadeOutTicks = 0;
    private static final int FADE_OUT_DURATION = 40; // 2 seconds fade

    private StardewMusicManager() {}

    // ────────────────────────── Public API ──────────────────────────

    /**
     * Called every client tick from ModClientEvents.
     */
    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            stopAll();
            return;
        }

        // Only manage music in Stardew dimensions
        boolean inStardew = mc.level.dimension() == ModDimensions.STARDEW_VALLEY;
        boolean inMine = mc.level.dimension() == ModMiningDimensions.STARDEW_MINING;
        if (!inStardew && !inMine) {
            stopAll();
            return;
        }

        // Suppress vanilla MC music when in Stardew dimensions
        mc.getMusicManager().stopPlaying();

        // Process fade-out
        tickFadeOut();

        // Periodic track evaluation
        tickCounter++;
        if (tickCounter >= CHECK_INTERVAL) {
            tickCounter = 0;
            evaluateAndPlay(mc, inMine);
        }
    }

    /**
     * Force stop all music immediately (e.g. on dimension change).
     */
    public static void stopAll() {
        if (currentMusic != null) {
            currentMusic.stopNow();
            Minecraft.getInstance().getSoundManager().stop(currentMusic);
            currentMusic = null;
            currentTrackEvent = null;
        }
        if (fadingOut != null) {
            fadingOut.stopNow();
            Minecraft.getInstance().getSoundManager().stop(fadingOut);
            fadingOut = null;
        }
        tickCounter = 0;
    }

    // ────────────────────────── Core Logic ──────────────────────────

    private static void evaluateAndPlay(Minecraft mc, boolean inMine) {
        SoundEvent desired = pickTrack(mc, inMine);
        if (desired == null) {
            LOG.debug("[StardewMusic] pickTrack returned null, stopping current music");
            // No suitable track -> stop
            if (currentMusic != null) {
                crossfadeTo(null);
            }
            return;
        }

        // Same track already playing?
        if (currentTrackEvent != null && currentTrackEvent.equals(desired)) {
            // Check if it actually stopped (e.g. finished or error)
            if (currentMusic != null && currentMusic.isStopped()) {
                LOG.debug("[StardewMusic] Restarting stopped track: {}", desired.getLocation());
                // Restart
                startPlaying(desired);
            }
            return;
        }

        // Different track -> crossfade
        LOG.debug("[StardewMusic] Crossfading to: {}", desired.getLocation());
        crossfadeTo(desired);
    }

    /**
     * Picks the appropriate music track based on current conditions.
     * Priority: Mine > Interior > Rain > Night ambient > Seasonal day music
     */
    @Nullable
    private static SoundEvent pickTrack(Minecraft mc, boolean inMine) {
        // ── Mine music ──
        if (inMine) {
            return pickMineTrack();
        }

        // ── Interior music (highest priority in Stardew overworld) ──
        SoundEvent interiorTrack = pickInteriorTrack(mc);
        if (interiorTrack != null) {
            return interiorTrack;
        }

        // ── Weather: Rain ──
        String weather = ClientWeatherCache.getCurrentWeather(ModDimensions.STARDEW_VALLEY);
        boolean isRaining = "Rain".equals(weather) || "Storm".equals(weather);
        if (isRaining) {
            return ModSounds.MUSIC_RAIN.get();
        }

        // ── Get time and season info ──
        StardewTimeManager timeCache = StardewTimeHud.getClientTimeCache();
        if (timeCache == null) {
            return null;
        }

        int currentTime = timeCache.getCurrentTime();
        int season = timeCache.getCurrentSeason();
        boolean isNight = currentTime >= NIGHT_TIME;

        // ── Night ambient (all non-winter seasons, after 18:00) ──
        if (isNight && season != 3) {
            return ModSounds.MUSIC_SPRING_NIGHT_AMBIENT.get();
        }

        // ── Daytime: pick a seasonal track ──
        // SDV rotates tracks by non-rainy day count
        int day = timeCache.getCurrentDay();
        return pickSeasonalTrack(season, day);
    }

    /**
     * Picks seasonal outdoor music. Rotates through 3 tracks per season.
     */
    private static SoundEvent pickSeasonalTrack(int season, int day) {
        int index = day % 3;
        return switch (season) {
            case 0 -> getSeasonal(ModSounds.MUSIC_SPRING1, ModSounds.MUSIC_SPRING2, ModSounds.MUSIC_SPRING3, index);
            case 1 -> getSeasonal(ModSounds.MUSIC_SUMMER1, ModSounds.MUSIC_SUMMER2, ModSounds.MUSIC_SUMMER3, index);
            case 2 -> getSeasonal(ModSounds.MUSIC_FALL1, ModSounds.MUSIC_FALL2, ModSounds.MUSIC_FALL3, index);
            case 3 -> getSeasonal(ModSounds.MUSIC_WINTER1, ModSounds.MUSIC_WINTER2, ModSounds.MUSIC_WINTER3, index);
            default -> null;
        };
    }

    private static SoundEvent getSeasonal(
            DeferredHolder<SoundEvent, SoundEvent> a,
            DeferredHolder<SoundEvent, SoundEvent> b,
            DeferredHolder<SoundEvent, SoundEvent> c,
            int index) {
        return switch (index) {
            case 0 -> a.get();
            case 1 -> b.get();
            default -> c.get();
        };
    }

    /**
     * Picks mine music based on current floor.
     * SDV: EarthMine (1-39) randomly picks Crystal Bells / Cavern / Secret Gnomes.
     *      FrostMine (40-79) randomly picks Cloth / Icicles / XOR.
     */
    private static SoundEvent pickMineTrack() {
        int floor = MiningFloorHud.getCurrentFloor();
        if (floor < FROST_MINE_START) {
            return ModSounds.MUSIC_EARTH_MINE.get();
        } else {
            return ModSounds.MUSIC_FROST_MINE.get();
        }
    }

    /**
     * Checks if the player is inside an interior subspace and returns the matching music.
     * Interior subspaces are at X/Z ∈ [10001, 19000] in the Stardew dimension.
     */
    @Nullable
    private static SoundEvent pickInteriorTrack(Minecraft mc) {
        if (mc.player == null) return null;

        int px = mc.player.getBlockX();
        int pz = mc.player.getBlockZ();

        // Fast reject: not in interior region
        if (px < INTERIOR_MIN || px > INTERIOR_MAX || pz < INTERIOR_MIN || pz > INTERIOR_MAX) {
            return null;
        }

        // Find the nearest interior origin
        InteriorEntry nearest = null;
        long bestDist = Long.MAX_VALUE;
        for (InteriorEntry entry : INTERIORS) {
            long dx = (long) px - entry.x;
            long dz = (long) pz - entry.z;
            long dist = dx * dx + dz * dz;
            if (dist < bestDist) {
                bestDist = dist;
                nearest = entry;
            }
        }

        if (nearest != null && nearest.music != null) {
            return nearest.music.get();
        }
        return null;
    }

    // ────────────────────────── Playback ──────────────────────────

    private static void crossfadeTo(@Nullable SoundEvent newTrack) {
        // Move current to fade-out
        if (currentMusic != null && !currentMusic.isStopped()) {
            if (fadingOut != null) {
                fadingOut.stopNow();
            }
            fadingOut = currentMusic;
            fadeOutTicks = 0;
        }
        currentMusic = null;
        currentTrackEvent = null;

        if (newTrack != null) {
            startPlaying(newTrack);
        }
    }

    private static void startPlaying(SoundEvent event) {
        LOG.info("[StardewMusic] Playing: {}", event.getLocation());
        StardewMusicInstance instance = new StardewMusicInstance(event);
        Minecraft.getInstance().getSoundManager().play(instance);
        currentMusic = instance;
        currentTrackEvent = event;
    }

    private static void tickFadeOut() {
        StardewMusicInstance fading = fadingOut;
        if (fading == null) return;

        fadeOutTicks++;
        float progress = (float) fadeOutTicks / FADE_OUT_DURATION;
        if (progress >= 1.0f) {
            fading.stopNow();
            Minecraft.getInstance().getSoundManager().stop(fading);
            fadingOut = null;
        } else {
            fading.setFadeVolume(1.0f - progress);
        }
    }

    // ────────────────────────── Sound Instance ──────────────────────────

    /**
     * A looping, streaming music sound instance with volume fade support.
     */
    public static final class StardewMusicInstance extends AbstractSoundInstance implements TickableSoundInstance {
        private volatile boolean stopped;
        private float fadeVolume = 1.0f;

        public StardewMusicInstance(SoundEvent sound) {
            super(sound, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0f;
            this.pitch = 1.0f;
            this.relative = true;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        }

        void setFadeVolume(float fade) {
            this.fadeVolume = Math.max(0.0f, Math.min(1.0f, fade));
        }

        public void stopNow() {
            this.stopped = true;
        }

        @Override
        public float getVolume() {
            return this.volume * this.fadeVolume;
        }

        @Override
        public boolean isStopped() {
            return stopped;
        }

        @Override
        public void tick() {
            // Volume fade is handled via getVolume() override
        }
    }
}
