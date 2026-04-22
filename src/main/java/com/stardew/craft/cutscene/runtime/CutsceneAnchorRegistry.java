package com.stardew.craft.cutscene.runtime;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side registry of named "anchor" origins for the currently playing cutscene.
 * <p>
 * Cutscene JSON commands may carry an {@code "anchor": "<name>"} field. When set,
 * the command's absolute coordinates (x/y/z) are interpreted as offsets relative
 * to the anchor's origin, which is supplied by the server (per-player) via
 * {@code CutsceneAnchorPayload}. This is required for cutscenes that play inside
 * per-player subspaces (Community Center interior, Greenhouse, Farm, ...).
 * <p>
 * Notes:
 * <ul>
 *   <li>If {@code relative=true} is set on a command, the anchor is ignored —
 *       relative=true means "relative to the actor's current position", which is
 *       a different concept.</li>
 *   <li>An unknown / unset anchor returns a zero offset (i.e. the command behaves
 *       as if the field was absent), so old absolute coordinates keep working.</li>
 * </ul>
 */
public final class CutsceneAnchorRegistry {

    private static final Map<String, Vec3> ORIGINS = new HashMap<>();

    private CutsceneAnchorRegistry() {}

    public static void set(String name, double x, double y, double z) {
        if (name == null || name.isEmpty()) return;
        ORIGINS.put(name, new Vec3(x, y, z));
    }

    public static void clear() {
        ORIGINS.clear();
    }

    /** Returns the anchor origin or null if unknown. */
    public static Vec3 get(String name) {
        return name == null ? null : ORIGINS.get(name);
    }

    public static double offsetX(String anchor) {
        Vec3 v = get(anchor);
        return v == null ? 0.0 : v.x;
    }

    public static double offsetY(String anchor) {
        Vec3 v = get(anchor);
        return v == null ? 0.0 : v.y;
    }

    public static double offsetZ(String anchor) {
        Vec3 v = get(anchor);
        return v == null ? 0.0 : v.z;
    }
}
