package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.FireRingEffectPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TemperedFireRingTracker {

    private static final class RingState {
        private final Vec3 center;
        private final float maxRadius;
        private final int durationTicks;
        private final long startTick;
        private final float damageMultiplier;
        private float lastRadius;
        private final Set<UUID> hitTargets = new HashSet<>();

        private RingState(Vec3 center, float maxRadius, int durationTicks, long startTick, float damageMultiplier) {
            this.center = center;
            this.maxRadius = maxRadius;
            this.durationTicks = durationTicks;
            this.startTick = startTick;
            this.damageMultiplier = damageMultiplier;
            this.lastRadius = 0.0f;
        }
    }

    private static final Map<UUID, List<RingState>> ACTIVE = new HashMap<>();

    private TemperedFireRingTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, Vec3 center, long nowTick, float maxRadius, int durationTicks) {
        if (player == null || maxRadius <= 0.0f || durationTicks <= 0) {
            return;
        }
        ACTIVE.computeIfAbsent(player.getUUID(), k -> new ArrayList<>())
            .add(new RingState(center, maxRadius, durationTicks, nowTick, 0.6f));

        if (player.level() instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel,
                new FireRingEffectPayload((float) center.x, (float) center.y, (float) center.z, maxRadius, durationTicks));
        }
    }

    public static void tick(ServerPlayer player, long nowTick) {
        List<RingState> rings = ACTIVE.get(player.getUUID());
        if (rings == null || rings.isEmpty()) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Iterator<RingState> iterator = rings.iterator();
        while (iterator.hasNext()) {
            RingState ring = iterator.next();
            long elapsed = nowTick - ring.startTick;
            if (elapsed < 0) {
                continue;
            }

            float t = ring.durationTicks <= 0 ? 1.0f : (elapsed / (float) ring.durationTicks);
            t = Math.max(0.0f, Math.min(1.0f, t));
            float radius = Math.max(0.25f, ring.maxRadius * t);

            if (radius > ring.lastRadius + 0.01f) {
                damageNewTargets(player, serverLevel, ring, radius);
                ring.lastRadius = radius;
            }

            if (elapsed > ring.durationTicks + 2) {
                iterator.remove();
            }
        }

        if (rings.isEmpty()) {
            ACTIVE.remove(player.getUUID());
        }
    }

    @SuppressWarnings("null")
    private static void damageNewTargets(Player owner, ServerLevel level, RingState ring, float radius) {
        AABB box = new AABB(
            ring.center.x - radius, ring.center.y - 1.0, ring.center.z - radius,
            ring.center.x + radius, ring.center.y + 2.0, ring.center.z + radius
        );
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive() && entity != owner
        );

        for (LivingEntity target : targets) {
            if (!ring.hitTargets.add(target.getUUID())) {
                continue;
            }
            double distSqr = target.distanceToSqr(ring.center.x, ring.center.y, ring.center.z);
            if (distSqr > radius * radius) {
                continue;
            }

            SkillContext context = SkillContext.builder()
                .skillId("tempered_billet")
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(ring.damageMultiplier)
                .build();
            WeaponSkillContextStore.setPending(owner, context, level.getGameTime() + 5);

            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(owner.damageSources().playerAttack(owner), 1.0F);
        }
    }
}
