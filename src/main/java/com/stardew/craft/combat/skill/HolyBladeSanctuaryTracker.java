package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.HolyBladeRingPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("null")
public final class HolyBladeSanctuaryTracker {

    private static final class SanctuaryState {
        private final float maxRadius;
        private long endTick;
        private long nextPulseTick;

        private SanctuaryState(Vec3 center, float maxRadius, long startTick, long endTick, long nextPulseTick) {
            this.maxRadius = maxRadius;
            this.endTick = endTick;
            this.nextPulseTick = nextPulseTick;
        }
    }

    private static final Map<UUID, SanctuaryState> ACTIVE = new HashMap<>();

    private HolyBladeSanctuaryTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, long nowTick, int durationTicks, float maxRadius) {
        if (player == null || durationTicks <= 0 || maxRadius <= 0.0f) {
            return;
        }
        Vec3 center = player.position();
        SanctuaryState state = new SanctuaryState(center, maxRadius, nowTick, nowTick + durationTicks, nowTick);
        ACTIVE.put(player.getUUID(), state);

        HolyBladeEffects.playDomainActivate(player);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        SanctuaryState state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick >= state.endTick) {
            ACTIVE.remove(player.getUUID());
            return;
        }

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (nowTick >= state.nextPulseTick) {
            state.nextPulseTick += 20;

            Vec3 center = player.position();
            float radius = state.maxRadius;
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new HolyBladeRingPayload((float) center.x, (float) center.y, (float) center.z, state.maxRadius, 12));
            List<LivingEntity> targets = getTargetsInRadius(serverLevel, center, radius, player);

            for (LivingEntity target : targets) {
                SkillContext context = SkillContext.builder()
                    .skillId("holy_domain")
                    .tier(SkillContext.SkillTier.MAJOR)
                    .damageMultiplier(0.75f)
                    .build();
                WeaponSkillContextStore.setPending(player, context, nowTick + 5);

                target.invulnerableTime = 0;
                target.hurtTime = 0;
                target.hurt(player.damageSources().playerAttack(player), 1.0F);

                HolyBladeEffects.playDomainPulse(serverLevel, target);
            }

            HolyBladeEffects.playHeal(player, 4);
        }
    }

    private static List<LivingEntity> getTargetsInRadius(ServerLevel level, Vec3 center, float radius, Player owner) {
        AABB box = new AABB(
            center.x - radius, center.y - radius * 0.6, center.z - radius,
            center.x + radius, center.y + radius * 0.6, center.z + radius
        );
        return level.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive() && entity != owner);
    }
}
