package com.stardew.craft.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AttackTargetTracker {
    private static final Map<UUID, AttackRecord> PRIMARY = new ConcurrentHashMap<>();
    private static final long MAX_AGE_TICKS = 2L;

    private AttackTargetTracker() {
    }

    public static void record(Player player, LivingEntity target, long tick) {
        if (player == null || target == null) {
            return;
        }
        PRIMARY.put(player.getUUID(), new AttackRecord(target.getUUID(), tick));
    }

    public static boolean isPrimaryTarget(Player player, LivingEntity target, long tick) {
        if (player == null || target == null) {
            return false;
        }
        AttackRecord record = PRIMARY.get(player.getUUID());
        if (record == null) {
            return false;
        }
        if (tick - record.tick > MAX_AGE_TICKS) {
            PRIMARY.remove(player.getUUID());
            return false;
        }
        return record.targetId.equals(target.getUUID());
    }

    private static final class AttackRecord {
        private final UUID targetId;
        private final long tick;

        private AttackRecord(UUID targetId, long tick) {
            this.targetId = targetId;
            this.tick = tick;
        }
    }
}
