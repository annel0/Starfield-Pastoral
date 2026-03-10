package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.ElfBladePayload;
import com.stardew.craft.entity.projectile.ElfBladeLeafEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ElfBladeTracker {

    private static final int LEAF_COUNT = 3;
    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private static final class State {
        private long endTick;
        private String weaponId;
        private String skillId;
        private int cooldownTicks;
        private boolean cooldownApplied;

        private State(long endTick, String weaponId, String skillId, int cooldownTicks) {
            this.endTick = endTick;
            this.weaponId = weaponId;
            this.skillId = skillId;
            this.cooldownTicks = cooldownTicks;
            this.cooldownApplied = false;
        }
    }

    private ElfBladeTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks, float damageMultiplier,
                             String weaponId, String skillId, int cooldownTicks) {
        if (player == null || durationTicks <= 0 || weaponId == null || skillId == null) {
            return;
        }

        long endTick = nowTick + durationTicks;
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            ACTIVE.put(player.getUUID(), new State(endTick, weaponId, skillId, cooldownTicks));
        } else {
            state.endTick = endTick;
            state.weaponId = weaponId;
            state.skillId = skillId;
            state.cooldownTicks = cooldownTicks;
            state.cooldownApplied = false;
        }

        PacketDistributor.sendToPlayer(player, new ElfBladePayload(true, durationTicks));
        spawnLeaves(player, endTick, damageMultiplier, skillId);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) {
            return;
        }
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }

        if (nowTick >= state.endTick) {
            if (!state.cooldownApplied && state.cooldownTicks > 0) {
                WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
                state.cooldownApplied = true;
            }
            PacketDistributor.sendToPlayer(player, new ElfBladePayload(false, 0));
            ACTIVE.remove(player.getUUID());
        }
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        if (player == null) {
            return false;
        }
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return false;
        }
        if (nowTick >= state.endTick) {
            ACTIVE.remove(player.getUUID());
            return false;
        }
        return true;
    }

    public static void fireLeafAtTarget(ServerPlayer player, LivingEntity target, long nowTick) {
        if (player == null || target == null) {
            return;
        }
        if (!isActive(player, nowTick)) {
            return;
        }
        ElfBladeLeafEntity leaf = findOrbitingLeaf(player);
        if (leaf != null) {
            leaf.launchToTarget(target);
        }
    }

    private static void spawnLeaves(ServerPlayer player, long endTick, float damageMultiplier, String skillId) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        clearLeaves(player);

        for (int i = 0; i < LEAF_COUNT; i++) {
            ElfBladeLeafEntity leaf = new ElfBladeLeafEntity(level, player, damageMultiplier, skillId, i, endTick);
            leaf.setPos(player.getX(), player.getY() + player.getBbHeight() * 0.6, player.getZ());
            level.addFreshEntity(leaf);
        }
    }

    @SuppressWarnings("null")
    private static ElfBladeLeafEntity findOrbitingLeaf(ServerPlayer player) {
        @SuppressWarnings("null")
        net.minecraft.world.phys.AABB box = player.getBoundingBox().inflate(6.0, 4.0, 6.0);
        List<ElfBladeLeafEntity> leaves = player.level().getEntitiesOfClass(
            ElfBladeLeafEntity.class,
            box,
            leaf -> leaf.isOrbiting() && leaf.getOwner() == player
        );
        if (leaves.isEmpty()) {
            return null;
        }
        ElfBladeLeafEntity best = null;
        int bestIndex = Integer.MAX_VALUE;
        for (ElfBladeLeafEntity leaf : leaves) {
            int idx = leaf.getOrbitIndex();
            if (best == null || idx < bestIndex) {
                best = leaf;
                bestIndex = idx;
            }
        }
        return best;
    }

    @SuppressWarnings("null")
    private static void clearLeaves(ServerPlayer player) {
        @SuppressWarnings("null")
        net.minecraft.world.phys.AABB box = player.getBoundingBox().inflate(8.0, 6.0, 8.0);
        List<ElfBladeLeafEntity> leaves = player.level().getEntitiesOfClass(
            ElfBladeLeafEntity.class,
            box,
            leaf -> leaf.getOwner() == player
        );
        for (ElfBladeLeafEntity leaf : leaves) {
            leaf.discard();
        }
    }
}
