package com.stardew.craft.entity.effect;

import com.stardew.craft.combat.skill.SkillContext;
import com.stardew.craft.combat.skill.WeaponSkillContextStore;
import com.stardew.craft.combat.skill.YetiToothEffects;
import com.stardew.craft.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("null")
public class IceSpineEffectEntity extends Entity {

    private static final double DEFAULT_MAX_DISTANCE = 6.0;
    private static final double SPEED = 0.5;

    private UUID ownerId;
    private float damageMultiplier = 1.8f;
    private String skillId = "yeti_tooth_spine";
    private Vec3 startPos = Vec3.ZERO;
    private int directFreezeTicks;
    private double maxDistance = DEFAULT_MAX_DISTANCE;
    private final Set<UUID> hitTargets = new HashSet<>();

    public IceSpineEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public IceSpineEffectEntity(Level level, LivingEntity owner, Vec3 start, Vec3 direction, float damageMultiplier, String skillId) {
        super(ModEntities.ICE_SPINE_EFFECT.get(), level);
        this.noPhysics = true;
        this.ownerId = owner != null ? owner.getUUID() : null;
        this.startPos = start;
        this.damageMultiplier = damageMultiplier;
        if (skillId != null) {
            this.skillId = skillId;
        }
        this.setPos(start.x, start.y, start.z);
        Vec3 vel = direction.normalize().scale(SPEED);
        this.setDeltaMovement(vel);
        this.setYRot((float) (Math.atan2(-vel.x, vel.z) * (180.0 / Math.PI)));
    }

    public IceSpineEffectEntity(Level level, LivingEntity owner, Vec3 start, Vec3 direction, int freezeTicks) {
        this(level, owner, start, direction, 0.0f, "ice_rod");
        this.directFreezeTicks = Math.max(1, freezeTicks);
        this.maxDistance = 3000.0 / 64.0;
        this.setYRot(this.getYRot() + 180.0F);
    }

    @Override
    protected void defineSynchedData(@SuppressWarnings("null") net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            double x = this.getX();
            double y = this.getY() + 0.2;
            double z = this.getZ();
            this.level().addParticle(ParticleTypes.SNOWFLAKE,
                x, y, z,
                (random.nextDouble() - 0.5) * 0.04,
                0.02,
                (random.nextDouble() - 0.5) * 0.04);
            if (random.nextFloat() < 0.4f) {
                this.level().addParticle(ParticleTypes.CLOUD,
                    x, y, z,
                    (random.nextDouble() - 0.5) * 0.02,
                    0.01,
                    (random.nextDouble() - 0.5) * 0.02);
            }
            return;
        }

        @SuppressWarnings("null")
        Vec3 delta = this.getDeltaMovement();
        this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);

        @SuppressWarnings("null")
        Vec3 pos = this.position();
        if (pos.distanceTo(startPos) >= maxDistance) {
            this.discard();
            return;
        }

        @SuppressWarnings("null")
        AABB box = this.getBoundingBox().inflate(0.4, 0.8, 0.4);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, e -> e.isPickable() && e.isAlive())) {
            if (!canHit(target)) {
                continue;
            }
            handleHit(target);
            hitTargets.add(target.getUUID());
        }
    }

    private boolean canHit(LivingEntity target) {
        if (isOwner(target) || hitTargets.contains(target.getUUID())) {
            return false;
        }
        if (directFreezeTicks > 0) {
            return target instanceof Enemy && !(target instanceof Player);
        }
        return true;
    }

    private boolean isOwner(LivingEntity target) {
        return ownerId != null && ownerId.equals(target.getUUID());
    }

    @SuppressWarnings("null")
    private void handleHit(LivingEntity target) {
        Entity owner = getOwnerEntity();
        if (!(owner instanceof Player player)) {
            return;
        }

        if (directFreezeTicks > 0) {
            if (target.level() instanceof ServerLevel serverLevel) {
                YetiToothEffects.applyFreeze(serverLevel, target, directFreezeTicks);
            }
            return;
        }

        long nowTick = this.level().getGameTime();
        SkillContext context = SkillContext.builder()
            .skillId(skillId)
            .tier(SkillContext.SkillTier.MAJOR)
            .damageMultiplier(damageMultiplier)
            .build();
        WeaponSkillContextStore.setPending(player, context, nowTick + 5);

        target.invulnerableTime = 0;
        target.hurtTime = 0;
        DamageSource source = player.damageSources().playerAttack(player);
        target.hurt(source, 1.0F);

        boolean hasSlow = target.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
        if (hasSlow && target.level() instanceof ServerLevel serverLevel) {
            YetiToothEffects.applyFreeze(serverLevel, target, 60);
        } else {
            YetiToothEffects.applySlow(target, 40, 1);
        }
    }

    @SuppressWarnings("null")
    private Entity getOwnerEntity() {
        if (ownerId == null) {
            return null;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getPlayerByUUID(ownerId);
        }
        return null;
    }

    @Override
    protected void readAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerId = tag.getUUID("Owner");
        }
        if (tag.contains("DamageMultiplier")) {
            this.damageMultiplier = tag.getFloat("DamageMultiplier");
        }
        if (tag.contains("SkillId")) {
            this.skillId = tag.getString("SkillId");
        }
        if (tag.contains("DirectFreezeTicks")) {
            this.directFreezeTicks = tag.getInt("DirectFreezeTicks");
        }
        if (tag.contains("MaxDistance")) {
            this.maxDistance = tag.getDouble("MaxDistance");
        }
        if (tag.contains("StartX")) {
            this.startPos = new Vec3(tag.getDouble("StartX"), tag.getDouble("StartY"), tag.getDouble("StartZ"));
        }
    }

    @Override
    @SuppressWarnings("null")
    protected void addAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
        if (ownerId != null) {
            tag.putUUID("Owner", ownerId);
        }
        tag.putFloat("DamageMultiplier", damageMultiplier);
        tag.putString("SkillId", skillId);
        tag.putInt("DirectFreezeTicks", directFreezeTicks);
        tag.putDouble("MaxDistance", maxDistance);
        tag.putDouble("StartX", startPos.x);
        tag.putDouble("StartY", startPos.y);
        tag.putDouble("StartZ", startPos.z);
    }

    @SuppressWarnings("null")
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(@SuppressWarnings("null") net.minecraft.server.level.ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }
}
