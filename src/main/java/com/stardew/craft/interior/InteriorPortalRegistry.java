package com.stardew.craft.interior;

import java.util.Map;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 室内入口/出口目标注册表。
 *
 * 你后续把每个房间的 scheme/nbt 坐标发来后，只需要在这里统一维护坐标，
 * 交互实体只放一个 tag: sdv_portal_target:<id>。
 */
public final class InteriorPortalRegistry {

    private static final Map<String, PortalTarget> TARGETS = new ConcurrentHashMap<>();

    private InteriorPortalRegistry() {
    }

    public static Optional<PortalTarget> resolve(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(TARGETS.get(id.trim().toLowerCase(Locale.ROOT)));
    }

    public static void register(String id, PortalTarget target) {
        if (id == null || id.isBlank() || target == null) {
            return;
        }
        TARGETS.put(id.trim().toLowerCase(Locale.ROOT), target);
    }

    public static record PortalTarget(
        double x,
        double y,
        double z,
        float yaw,
        float pitch,
        PortalMode mode
    ) {}

    public enum PortalMode {
        ENTRANCE,
        EXIT,
        NONE
    }
}
