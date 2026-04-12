package com.stardew.craft.combat.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import com.stardew.craft.combat.VfxColors;
import com.stardew.craft.combat.network.ShockwaveRingPayload;
import com.stardew.craft.combat.network.StarfallMeteorPayload;
import com.stardew.craft.combat.network.StarfallShockwavePostPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 银河剑 - 星落打击：延迟多段落击
 */
public final class StarfallTracker {

    private static final int STRIKE_INTERVAL_TICKS = 10;

    private static final class State {
        private long nextStrikeTick;
        private int remainingStrikes;
        private final int extraHits;
        private final double radius;
        private final float damageMultiplier;
        private final String skillId;

        private State(long nextStrikeTick, int remainingStrikes, int extraHits, double radius, float damageMultiplier, String skillId) {
            this.nextStrikeTick = nextStrikeTick;
            this.remainingStrikes = remainingStrikes;
            this.extraHits = extraHits;
            this.radius = radius;
            this.damageMultiplier = damageMultiplier;
            this.skillId = skillId;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private StarfallTracker() {}

    public static void start(ServerPlayer player, long nowTick, int strikes, int extraHits, double radius,
                             float damageMultiplier, String skillId) {
        if (player == null || strikes <= 0) {
            return;
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + STRIKE_INTERVAL_TICKS, strikes, extraHits, radius, damageMultiplier, skillId));
    }

    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick < state.nextStrikeTick) {
            return;
        }

        strike(player, nowTick, state);
        state.remainingStrikes -= 1;
        if (state.remainingStrikes <= 0) {
            ACTIVE.remove(player.getUUID());
        } else {
            state.nextStrikeTick = nowTick + STRIKE_INTERVAL_TICKS;
        }
    }

    @SuppressWarnings("null")
    private static void strike(ServerPlayer player, long nowTick, State state) {
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position();
        AABB box = new AABB(
            center.x - state.radius, center.y - 1.5, center.z - state.radius,
            center.x + state.radius, center.y + 2.0, center.z + state.radius
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity.isPickable() && entity != player
        );

        int hits = 1 + Math.max(0, state.extraHits);
        for (LivingEntity target : targets) {
            for (int i = 0; i < hits; i++) {
                SkillContext context = SkillContext.builder()
                    .skillId(state.skillId)
                    .tier(SkillContext.SkillTier.MAJOR)
                    .damageMultiplier(state.damageMultiplier)
                    .build();
                WeaponSkillContextStore.setPending(player, context, nowTick + 5);
                target.invulnerableTime = 0;
                target.hurtTime = 0;
                player.attack(target);
            }
        }

        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8f, 1.2f);
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.4f);

        level.sendParticles(ParticleTypes.END_ROD,
            center.x, center.y + 1.2, center.z,
            18, state.radius * 0.35, 0.6, state.radius * 0.35, 0.02);
        level.sendParticles(ParticleTypes.ENCHANT,
            center.x, center.y + 0.8, center.z,
            12, state.radius * 0.35, 0.4, state.radius * 0.35, 0.02);
        level.sendParticles(ParticleTypes.CRIT,
            center.x, center.y + 0.4, center.z,
            16, state.radius * 0.45, 0.35, state.radius * 0.45, 0.08);

        PacketDistributor.sendToPlayersInDimension(level,
            new ShockwaveRingPayload((float) center.x, (float) center.y, (float) center.z, (float) state.radius, 8,
                VfxColors.GALAXY_PURPLE));

        PacketDistributor.sendToPlayersInDimension(level,
            new StarfallMeteorPayload((float) center.x, (float) center.y, (float) center.z, 6.0f, 14,
                VfxColors.GALAXY_PURPLE));

        PacketDistributor.sendToPlayersInDimension(level,
            new StarfallShockwavePostPayload((float) center.x, (float) center.y + 0.2f, (float) center.z, 0.28f, 0.9f, 8));
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
