package com.stardew.craft.entity.projectile;

import com.stardew.craft.combat.skill.SkillContext;
import com.stardew.craft.combat.skill.TideMarkTracker;
import com.stardew.craft.combat.skill.WeaponSkillContextStore;
import com.stardew.craft.combat.network.WaterRingEffectPayload;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.item.weapon.IStardewWeapon;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class TideAnchorProjectileEntity extends ThrowableProjectile {

    private static final double GRAVITY = 0.03;
    private static final double AOE_RADIUS = 4.5;
    private static final double MARK_TELEPORT_RADIUS = 24.0;
    private String skillId = "tide_anchor";

    public TideAnchorProjectileEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public TideAnchorProjectileEntity(Level level, LivingEntity owner, String skillId) {
        super(ModEntities.TIDE_ANCHOR_PROJECTILE.get(), owner, level);
        if (skillId != null) {
            this.skillId = skillId;
        }
    }

    @Override
    protected void defineSynchedData(@SuppressWarnings("null") net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(delta.x, delta.y * 0.99 - GRAVITY, delta.z);
        }
        if (this.tickCount > 80) {
            this.discard();
        }
    }

    @SuppressWarnings("null")
    @Override
    protected void onHitEntity(@SuppressWarnings("null") EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            handleImpact(result.getLocation());
        }
        this.discard();
    }

    @SuppressWarnings("null")
    @Override
    protected void onHitBlock(@SuppressWarnings("null") BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            handleImpact(result.getLocation());
        }
        this.discard();
    }

    @SuppressWarnings("null")
    private void handleImpact(Vec3 hitPos) {
        if (!(this.getOwner() instanceof Player player)) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof @SuppressWarnings("unused") IStardewWeapon weaponItem)) {
            return;
        }
        long nowTick = this.level().getGameTime();
        ServerLevel serverLevel = this.level() instanceof ServerLevel ? (ServerLevel) this.level() : null;

        // 水环特效（客户端表现）
        if (serverLevel != null) {
            PacketDistributor.sendToPlayersInDimension(serverLevel,
                new WaterRingEffectPayload((float) hitPos.x, (float) hitPos.y, (float) hitPos.z, (float) AOE_RADIUS, 20));
        }

        // 主要冲击音效与粒子
        if (serverLevel != null) {
            serverLevel.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 0.9f, 0.95f);
            serverLevel.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.PLAYERS, 0.8f, 1.1f);
            serverLevel.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.7f, 0.8f);

            serverLevel.sendParticles(ParticleTypes.SPLASH,
                hitPos.x, hitPos.y + 0.25, hitPos.z,
                26, 0.9, 0.25, 0.9, 0.04);
            serverLevel.sendParticles(ParticleTypes.BUBBLE,
                hitPos.x, hitPos.y + 0.15, hitPos.z,
                18, 0.7, 0.2, 0.7, 0.02);
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                hitPos.x, hitPos.y + 0.2, hitPos.z,
                10, 0.6, 0.1, 0.6, 0.02);
            serverLevel.sendParticles(ParticleTypes.CRIT,
                hitPos.x, hitPos.y + 0.3, hitPos.z,
                8, 0.5, 0.2, 0.5, 0.06);
        }

        // AOE 伤害
        AABB box = new AABB(
            hitPos.x - AOE_RADIUS, hitPos.y - 1.0, hitPos.z - AOE_RADIUS,
            hitPos.x + AOE_RADIUS, hitPos.y + 2.0, hitPos.z + AOE_RADIUS
        );
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity != player
        );

        for (LivingEntity target : targets) {
            SkillContext context = SkillContext.builder()
                .skillId(skillId)
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(1.5f)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            target.hurt(player.damageSources().playerAttack(player), 1.0F);
        }

        // 传送带印记的目标到锚点并禁锢
        LivingEntity marked = findNearestMarkedTarget(player, hitPos);
        if (marked != null) {
            Vec3 oldPos = marked.position();
            marked.teleportTo(hitPos.x, hitPos.y, hitPos.z);
            marked.setDeltaMovement(0, marked.getDeltaMovement().y, 0);
            marked.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 4, false, true, true));
            marked.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 128, false, false, false));

            if (serverLevel != null) {
                serverLevel.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                    SoundEvents.TRIDENT_RIPTIDE_1.value(), SoundSource.PLAYERS, 0.6f, 1.2f);
                serverLevel.sendParticles(ParticleTypes.SPLASH,
                    oldPos.x, oldPos.y + marked.getBbHeight() * 0.6, oldPos.z,
                    12, 0.5, 0.25, 0.5, 0.03);
                serverLevel.sendParticles(ParticleTypes.SPLASH,
                    hitPos.x, hitPos.y + marked.getBbHeight() * 0.6, hitPos.z,
                    14, 0.6, 0.25, 0.6, 0.03);
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    hitPos.x, hitPos.y + marked.getBbHeight() * 0.6, hitPos.z,
                    10, 0.4, 0.3, 0.4, 0.02);
            }
        }
    }

    private LivingEntity findNearestMarkedTarget(Player player, Vec3 anchorPos) {
        AABB box = new AABB(
            anchorPos.x - MARK_TELEPORT_RADIUS, anchorPos.y - 6.0, anchorPos.z - MARK_TELEPORT_RADIUS,
            anchorPos.x + MARK_TELEPORT_RADIUS, anchorPos.y + 6.0, anchorPos.z + MARK_TELEPORT_RADIUS
        );
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity != player
        );

        LivingEntity closest = null;
        double best = Double.MAX_VALUE;
        long nowTick = this.level().getGameTime();
        for (LivingEntity target : targets) {
            if (!TideMarkTracker.isMarked(target, nowTick)) {
                continue;
            }
            double dist = target.distanceToSqr(anchorPos.x, anchorPos.y, anchorPos.z);
            if (dist < best) {
                best = dist;
                closest = target;
            }
        }
        return closest;
    }

    @SuppressWarnings("null")
    @Override
    public void addAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("SkillId", this.skillId == null ? "" : this.skillId);
    }

    @SuppressWarnings("null")
    @Override
    public void readAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        String id = tag.getString("SkillId");
        this.skillId = id == null || id.isEmpty() ? "tide_anchor" : id;
    }
}

