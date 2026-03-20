package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.interior.InteriorPortalRegistry;
import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class InteriorPortalInteractionEvents {

    private static final String TAG_TARGET_PREFIX = "sdv_portal_target:";
    private static final String TAG_TO_PREFIX = "sdv_portal_to:";
    private static final String TAG_ROT_PREFIX = "sdv_portal_rot:";
    private static final String TAG_MODE_PREFIX = "sdv_portal_mode:";

    private static final String PLAYER_FLAG_INTERIOR = "stardewcraft_interior_space";
    private static final String PLAYER_LAST_PORTAL_TICK = "stardewcraft_last_portal_tick";

    private static final long PORTAL_COOLDOWN_TICKS = 8L;

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(player.serverLevel().dimension())) {
            return;
        }

        Entity target = event.getTarget();
        Optional<PortalTargetSpec> spec = resolveTargetSpec(target.getTags());
        if (spec.isEmpty()) {
            return;
        }

        // 对齐矿井大厅策略：交互传送前先确保室内布局已初始化。
        InteriorSubspaceManager.ensureLoaded(player.serverLevel(), "portal_interaction");

        long now = player.serverLevel().getGameTime();
        long last = player.getPersistentData().getLong(PLAYER_LAST_PORTAL_TICK);
        if (now - last < PORTAL_COOLDOWN_TICKS) {
            event.setCanceled(true);
            return;
        }

        PortalTargetSpec targetSpec = spec.get();
        player.teleportTo(
            player.serverLevel(),
            targetSpec.x,
            targetSpec.y,
            targetSpec.z,
            targetSpec.yaw,
            targetSpec.pitch
        );

        applyInteriorFlag(player, targetSpec.mode);
        player.getPersistentData().putLong(PLAYER_LAST_PORTAL_TICK, now);
        event.setCanceled(true);
    }

    private static Optional<PortalTargetSpec> resolveTargetSpec(Set<String> tags) {
        Optional<String> targetId = findTagValue(tags, TAG_TARGET_PREFIX);
        if (targetId.isPresent()) {
            Optional<InteriorPortalRegistry.PortalTarget> resolved = InteriorPortalRegistry.resolve(targetId.get());
            if (resolved.isPresent()) {
                InteriorPortalRegistry.PortalTarget t = resolved.get();
                return Optional.of(new PortalTargetSpec(t.x(), t.y(), t.z(), t.yaw(), t.pitch(), t.mode()));
            }
            return Optional.empty();
        }

        Optional<String> toValue = findTagValue(tags, TAG_TO_PREFIX);
        if (toValue.isEmpty()) {
            return Optional.empty();
        }

        double[] xyz = parseDoubles(toValue.get(), 3);
        if (xyz == null) {
            return Optional.empty();
        }

        float yaw = 0.0F;
        float pitch = 0.0F;
        Optional<String> rotValue = findTagValue(tags, TAG_ROT_PREFIX);
        if (rotValue.isPresent()) {
            double[] rot = parseDoubles(rotValue.get(), 2);
            if (rot != null) {
                yaw = (float) rot[0];
                pitch = (float) rot[1];
            }
        }

        InteriorPortalRegistry.PortalMode mode = findTagValue(tags, TAG_MODE_PREFIX)
            .map(InteriorPortalInteractionEvents::parseMode)
            .orElse(InteriorPortalRegistry.PortalMode.NONE);

        return Optional.of(new PortalTargetSpec(
            xyz[0] + 0.5D,
            xyz[1],
            xyz[2] + 0.5D,
            yaw,
            pitch,
            mode
        ));
    }

    private static Optional<String> findTagValue(Set<String> tags, String prefix) {
        for (String tag : tags) {
            if (tag != null && tag.startsWith(prefix) && tag.length() > prefix.length()) {
                return Optional.of(tag.substring(prefix.length()));
            }
        }
        return Optional.empty();
    }

    private static double[] parseDoubles(String value, int expectedParts) {
        String[] parts = value.split(",");
        if (parts.length != expectedParts) {
            return null;
        }
        double[] parsed = new double[expectedParts];
        try {
            for (int i = 0; i < expectedParts; i++) {
                parsed[i] = Double.parseDouble(parts[i].trim());
            }
            return parsed;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static InteriorPortalRegistry.PortalMode parseMode(String raw) {
        if (raw == null) {
            return InteriorPortalRegistry.PortalMode.NONE;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "entrance", "enter", "in" -> InteriorPortalRegistry.PortalMode.ENTRANCE;
            case "exit", "out" -> InteriorPortalRegistry.PortalMode.EXIT;
            default -> InteriorPortalRegistry.PortalMode.NONE;
        };
    }

    private static void applyInteriorFlag(ServerPlayer player, InteriorPortalRegistry.PortalMode mode) {
        if (mode == InteriorPortalRegistry.PortalMode.ENTRANCE) {
            player.getPersistentData().putBoolean(PLAYER_FLAG_INTERIOR, true);
            return;
        }
        if (mode == InteriorPortalRegistry.PortalMode.EXIT) {
            player.getPersistentData().putBoolean(PLAYER_FLAG_INTERIOR, false);
        }
    }

    public static boolean isPlayerInInteriorSpace(ServerPlayer player) {
        return player.getPersistentData().getBoolean(PLAYER_FLAG_INTERIOR);
    }

    private record PortalTargetSpec(
        double x,
        double y,
        double z,
        float yaw,
        float pitch,
        InteriorPortalRegistry.PortalMode mode
    ) {}
}
