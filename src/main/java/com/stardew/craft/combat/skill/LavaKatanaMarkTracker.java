package com.stardew.craft.combat.skill;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.combat.network.LavaKatanaMarkPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class LavaKatanaMarkTracker {

    private static final String TAG_END_TICK = "stardewcraft_lava_katana_mark_until";
    private static final String TAG_OWNER = "stardewcraft_lava_katana_mark_owner";
    private static final String TAG_HEAT = "stardewcraft_lava_katana_mark_heat";
    private static final String TAG_NEXT_TICK = "stardewcraft_lava_katana_mark_next_tick";

    private static final int HEAT_CAP = 5;
    private static final long BURN_INTERVAL = 10L;
    private static final float BASE_BURN_RATIO = 0.15f;
    private static final float HEAT_BONUS_RATIO = 0.04f;
    private static final float HEAT_BONUS_REVERB_RATIO = 0.08f;

    private static final Map<UUID, Set<UUID>> MARKED_BY_OWNER = new ConcurrentHashMap<>();

    private LavaKatanaMarkTracker() {}

    @SuppressWarnings("null")
    public static void apply(LivingEntity target, ServerPlayer owner, long nowTick, int durationTicks) {
        if (target == null || owner == null || durationTicks <= 0) {
            return;
        }

        CompoundTag tag = target.getPersistentData();
        tag.putLong(TAG_END_TICK, nowTick + durationTicks);
        tag.putUUID(TAG_OWNER, owner.getUUID());
        tag.putInt(TAG_HEAT, 0);
        tag.putLong(TAG_NEXT_TICK, nowTick + BURN_INTERVAL);

        MARKED_BY_OWNER
            .computeIfAbsent(owner.getUUID(), id -> ConcurrentHashMap.newKeySet())
            .add(target.getUUID());

        sendMarkSync(target, nowTick);

        if (target.level() instanceof ServerLevel serverLevel) {
            double x = target.getX();
            double y = target.getY() + target.getBbHeight() * 0.6;
            double z = target.getZ();
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                x, y, z,
                8, 0.25, 0.2, 0.25, 0.02);
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
                x, y, z,
                10, 0.35, 0.2, 0.35, 0.02);
            serverLevel.playSound(null, target.blockPosition(),
                SoundEvents.LAVA_POP,
                SoundSource.PLAYERS, 0.7f, 1.05f);
            serverLevel.playSound(null, target.blockPosition(),
                SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 0.6f, 1.1f);
        }
    }

    public static boolean isMarked(LivingEntity target, long nowTick) {
        CompoundTag tag = target.getPersistentData();
        if (!tag.contains(TAG_END_TICK)) {
            return false;
        }
        long endTick = tag.getLong(TAG_END_TICK);
        if (nowTick >= endTick) {
            clearMark(target, tag);
            return false;
        }
        return true;
    }

    public static boolean isMarkedBy(LivingEntity target, Player player, long nowTick) {
        if (!isMarked(target, nowTick)) {
            return false;
        }
        CompoundTag tag = target.getPersistentData();
        if (!tag.hasUUID(TAG_OWNER)) {
            return false;
        }
        UUID ownerId = tag.getUUID(TAG_OWNER);
        return ownerId.equals(player.getUUID());
    }

    public static int getHeat(LivingEntity target) {
        CompoundTag tag = target.getPersistentData();
        return Math.max(0, tag.getInt(TAG_HEAT));
    }

    public static int addHeatIfEligible(LivingEntity target, ServerPlayer owner, long nowTick, int amount) {
        if (!isMarkedBy(target, owner, nowTick)) {
            return 0;
        }
        CompoundTag tag = target.getPersistentData();
        int current = Math.max(0, tag.getInt(TAG_HEAT));
        boolean ignoreCap = LavaKatanaReverbTracker.isActive(owner, nowTick);
        int maxHeat = ignoreCap ? Integer.MAX_VALUE : HEAT_CAP;
        long nextHeat = (long) current + Math.max(0, amount);
        int clamped = (int) Math.min(maxHeat, Math.min(Integer.MAX_VALUE, nextHeat));
        if (clamped != current) {
            tag.putInt(TAG_HEAT, clamped);
            sendMarkSync(target, nowTick);
        }
        return clamped;
    }

    public static void ensureHeatAtLeast(LivingEntity target, ServerPlayer owner, long nowTick, int minHeat) {
        if (!isMarkedBy(target, owner, nowTick)) {
            return;
        }
        CompoundTag tag = target.getPersistentData();
        int current = Math.max(0, tag.getInt(TAG_HEAT));
        int next = Math.max(current, Math.max(0, minHeat));
        if (next != current) {
            tag.putInt(TAG_HEAT, next);
            sendMarkSync(target, nowTick);
        }
    }

    public static int getRemainingTicks(LivingEntity target, long nowTick) {
        CompoundTag tag = target.getPersistentData();
        if (!tag.contains(TAG_END_TICK)) {
            return 0;
        }
        return (int) Math.max(0, tag.getLong(TAG_END_TICK) - nowTick);
    }

    public static Set<UUID> getMarkedTargets(UUID ownerId) {
        Set<UUID> targets = MARKED_BY_OWNER.get(ownerId);
        if (targets == null || targets.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(targets);
    }

    public static void clearMark(LivingEntity target) {
        CompoundTag tag = target.getPersistentData();
        clearMark(target, tag);
    }

    private static void clearMark(LivingEntity target, CompoundTag tag) {
        UUID ownerId = tag.hasUUID(TAG_OWNER) ? tag.getUUID(TAG_OWNER) : null;
        UUID targetId = target.getUUID();
        tag.remove(TAG_END_TICK);
        tag.remove(TAG_OWNER);
        tag.remove(TAG_HEAT);
        tag.remove(TAG_NEXT_TICK);
        if (ownerId != null) {
            removeMarkedTarget(ownerId, targetId);
        }
        if (!target.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                target,
                new LavaKatanaMarkPayload(target.getId(), 0, 0)
            );
        }
    }

    private static void removeMarkedTarget(UUID ownerId, UUID targetId) {
        Set<UUID> targets = MARKED_BY_OWNER.get(ownerId);
        if (targets == null) {
            return;
        }
        targets.remove(targetId);
        if (targets.isEmpty()) {
            MARKED_BY_OWNER.remove(ownerId);
        }
    }

    private static void sendMarkSync(LivingEntity target, long nowTick) {
        if (target.level().isClientSide) {
            return;
        }
        int remaining = getRemainingTicks(target, nowTick);
        int heat = getHeat(target);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
            target,
            new LavaKatanaMarkPayload(target.getId(), remaining, heat)
        );
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity.level().isClientSide) {
            return;
        }
        CompoundTag tag = entity.getPersistentData();
        if (!tag.contains(TAG_END_TICK)) {
            return;
        }
        long nowTick = entity.level().getGameTime();
        long endTick = tag.getLong(TAG_END_TICK);
        if (nowTick >= endTick || !entity.isAlive()) {
            clearMark(entity, tag);
            return;
        }

        long nextTick = tag.getLong(TAG_NEXT_TICK);
        if (nowTick >= nextTick) {
            tag.putLong(TAG_NEXT_TICK, nowTick + BURN_INTERVAL);
            applyBurnTick(entity, tag, nowTick);
        }
    }

    @SuppressWarnings("null")
    private static void applyBurnTick(LivingEntity target, CompoundTag tag, long nowTick) {
        if (!tag.hasUUID(TAG_OWNER)) {
            return;
        }
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Player ownerPlayer = serverLevel.getPlayerByUUID(tag.getUUID(TAG_OWNER));
        if (!(ownerPlayer instanceof ServerPlayer owner)) {
            return;
        }

        int heat = Math.max(0, tag.getInt(TAG_HEAT));
        boolean reverbActive = LavaKatanaReverbTracker.isActive(owner, nowTick);
        int effectiveHeat = reverbActive ? heat : Math.min(heat, HEAT_CAP);
        float bonus = reverbActive ? HEAT_BONUS_REVERB_RATIO : HEAT_BONUS_RATIO;
        float damageMultiplier = BASE_BURN_RATIO + (effectiveHeat * bonus);

        SkillContext context = SkillContext.builder()
            .skillId("lava_katana_burn")
            .tier(SkillContext.SkillTier.MINOR)
            .damageMultiplier(damageMultiplier)
            .build();
        WeaponSkillContextStore.setPending(owner, context, nowTick + 5);

        target.invulnerableTime = 0;
        target.hurtTime = 0;
        target.hurt(owner.damageSources().playerAttack(owner), 1.0F);

        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.55;
        double z = target.getZ();
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME,
            x, y, z,
            6, 0.25, 0.18, 0.25, 0.01);
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
            x, y, z,
            4, 0.2, 0.15, 0.2, 0.01);
        serverLevel.playSound(null, target.blockPosition(),
            SoundEvents.FIRE_AMBIENT,
            SoundSource.PLAYERS, 0.5f, 1.2f);
    }
}
