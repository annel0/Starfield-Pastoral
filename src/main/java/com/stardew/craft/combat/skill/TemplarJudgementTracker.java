package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.TemplarJudgementImpactPayload;
import com.stardew.craft.combat.network.TemplarMarkPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TemplarJudgementTracker {

    private static final class State {
        private long endTick;
        private final List<Integer> targetIds;

        private State(long endTick, List<Integer> targetIds) {
            this.endTick = endTick;
            this.targetIds = targetIds;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private TemplarJudgementTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, long nowTick, int durationTicks, List<LivingEntity> targets) {
        if (player == null || durationTicks <= 0 || targets == null || targets.isEmpty()) {
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (LivingEntity target : targets) {
            ids.add(target.getId());
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + durationTicks, ids));

        if (player.level() instanceof ServerLevel serverLevel) {
            for (LivingEntity target : targets) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(target,
                    new TemplarMarkPayload(target.getId(), durationTicks));
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    target.getX(), target.getY() + target.getBbHeight() * 0.6, target.getZ(),
                    6, 0.4, 0.2, 0.4, 0.01);
            }
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.6f, 1.7f);
        }
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        return state != null && nowTick < state.endTick;
    }

    public static List<LivingEntity> getMarkedTargets(Player player) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null || player.level() == null) {
            return List.of();
        }
        List<LivingEntity> targets = new ArrayList<>();
        for (Integer id : state.targetIds) {
            var entity = player.level().getEntity(id);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                targets.add(living);
            }
        }
        return targets;
    }

    @SuppressWarnings("null")
    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick < state.endTick) {
            return;
        }

        List<LivingEntity> targets = getMarkedTargets(player);
        if (!targets.isEmpty()) {
            for (LivingEntity target : targets) {
                SkillContext context = SkillContext.builder()
                    .skillId("templar_judgement")
                    .tier(SkillContext.SkillTier.MAJOR)
                    .damageMultiplier(1.6f)
                    .build();
                WeaponSkillContextStore.setPending(player, context, nowTick + 5);
                target.invulnerableTime = 0;
                target.hurtTime = 0;
                target.hurt(player.damageSources().playerAttack(player), 1.0F);

                if (player.level() instanceof ServerLevel serverLevel) {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(target,
                        new TemplarJudgementImpactPayload(target.getId()));
                    serverLevel.playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.6f, 0.9f);
                }
            }
        }

        ACTIVE.remove(player.getUUID());
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
