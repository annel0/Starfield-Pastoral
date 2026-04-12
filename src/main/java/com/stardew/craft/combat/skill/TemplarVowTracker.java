package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.TemplarVowPayload;
import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TemplarVowTracker {

    private static final class State {
        private long endTick;
        private final String weaponId;
        private final String skillId;
        private final int cooldownTicks;
        private boolean cooldownApplied;

        private State(long endTick, String weaponId, String skillId, int cooldownTicks) {
            this.endTick = endTick;
            this.weaponId = weaponId;
            this.skillId = skillId;
            this.cooldownTicks = cooldownTicks;
            this.cooldownApplied = false;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private TemplarVowTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks, String weaponId, String skillId, int cooldownTicks) {
        if (player == null || durationTicks <= 0 || weaponId == null || skillId == null) {
            return;
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + durationTicks, weaponId, skillId, cooldownTicks));
        PacketDistributor.sendToPlayer(player, new TemplarVowPayload(true, durationTicks));
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        return state != null && nowTick <= state.endTick;
    }

    public static boolean isActiveRaw(ServerPlayer player) {
        return ACTIVE.containsKey(player.getUUID());
    }

    public static void endNow(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        applyCooldown(player, state, nowTick);
        PacketDistributor.sendToPlayer(player, new TemplarVowPayload(false, 0));
        ACTIVE.remove(player.getUUID());
    }

    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick > state.endTick) {
            applyLightSlash(player, nowTick);
            player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
            applyCooldown(player, state, nowTick);
            PacketDistributor.sendToPlayer(player, new TemplarVowPayload(false, 0));
            ACTIVE.remove(player.getUUID());
        }
    }

    private static void applyCooldown(ServerPlayer player, State state, long nowTick) {
        if (!state.cooldownApplied && state.cooldownTicks > 0) {
            WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
            state.cooldownApplied = true;
        }
    }

    @SuppressWarnings("null")
    private static void applyLightSlash(ServerPlayer player, long nowTick) {
        if (player == null) {
            return;
        }

        MobEffect shelter = Objects.requireNonNull(ModMobEffects.SHELTER.get(), "shelter");
        Holder<MobEffect> shelterHolder = Holder.direct(shelter);
        player.addEffect(new MobEffectInstance(shelterHolder, 40, 0, false, false, true));

        LivingEntity target = findTargetEntity(player, 4.0);
        if (target == null) {
            return;
        }

        SkillContext context = SkillContext.builder()
            .skillId("templar_vow")
            .tier(SkillContext.SkillTier.MINOR)
            .damageMultiplier(0.8f)
            .build();
        WeaponSkillContextStore.setPending(player, context, nowTick + 5);
        player.attack(target);
    }

    private static LivingEntity findTargetEntity(ServerPlayer player, double range) {
        Level level = Objects.requireNonNull(player.level(), "level");
        Vec3 eyePos = Objects.requireNonNull(player.getEyePosition(), "eyePos");
        Vec3 lookVec = Objects.requireNonNull(player.getLookAngle(), "lookVec");
        Vec3 scaledLook = Objects.requireNonNull(lookVec.scale(range), "scaledLook");
        Vec3 end = Objects.requireNonNull(eyePos.add(scaledLook), "end");
        Vec3 scaledBox = Objects.requireNonNull(lookVec.scale(range), "scaledBox");
        AABB box = Objects.requireNonNull(player.getBoundingBox().expandTowards(scaledBox).inflate(1.0D), "box");

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            level,
            player,
            eyePos,
            end,
            box,
            entity -> entity instanceof LivingEntity && entity.isPickable() && entity != player
        );

        return hit != null ? (LivingEntity) hit.getEntity() : null;
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
