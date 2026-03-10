package com.stardew.craft.entity.projectile;

import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.combat.skill.SkillContext;
import com.stardew.craft.combat.skill.WeaponSkillContextStore;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayDeque;
import java.util.Deque;

public class MeowmereProjectileEntity extends ThrowableProjectile {

    @SuppressWarnings("null")
    private static final EntityDataAccessor<Integer> BOUNCES = SynchedEntityData.defineId(MeowmereProjectileEntity.class, EntityDataSerializers.INT);
    private static final int MAX_BOUNCES = 4; // 最多反弹次数 (泰拉瑞亚原版是4次)
    private static final double GRAVITY_FACTOR = 0.03; // 低重力
    private float damage = 10.0f;
    private int pierceCount = 0; // 穿透次数
    private String skillId = null;
    private static final int TRAIL_MAX_AGE = 5; // ticks
    private static final int TRAIL_MAX_POINTS = 5;
    private static final float TRAIL_MIN_SPEED = 0.001f;
    private static final int TRAIL_UPDATE_FREQUENCY = 1;
    private static final double TRAIL_MOTION_SHIFT = 0.0;
    private static final Vec3 TRAIL_POSITION_OFFSET = new Vec3(0.0, 0.0, 0.0);
    private final Deque<TrailPoint> trailPoints = new ArrayDeque<>();

    public MeowmereProjectileEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public MeowmereProjectileEntity(Level level, LivingEntity owner, float damage, int pierceCount, String skillId) {
        super(ModEntities.MEOWMERE_PROJECTILE.get(), owner, level);
        this.damage = damage;
        this.pierceCount = pierceCount;
        this.skillId = skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    @SuppressWarnings("null")
    @Override
    protected void defineSynchedData(@SuppressWarnings("null") SynchedEntityData.Builder builder) {
        builder.define(BOUNCES, 0);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setPierceCount(int count) {
        this.pierceCount = count;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            recordTrailPoint();
        }

        if (!this.level().isClientSide) {
            // 简单的低重力控制
            Vec3 deltaMovement = this.getDeltaMovement();
            if (!this.isNoGravity()) {
                this.setDeltaMovement(deltaMovement.x, deltaMovement.y * 0.99 - GRAVITY_FACTOR, deltaMovement.z);
            }
        
            if (this.tickCount > 200) { // 10秒后消失
                this.discard();
            }
        }
    }

    @SuppressWarnings("null")
    private void recordTrailPoint() {
        if (this.tickCount % TRAIL_UPDATE_FREQUENCY != 0) {
            return;
        }

        Vec3 pos = this.tickCount > 1 ? this.getPosition(1.0f) : this.position();
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() < (double) (TRAIL_MIN_SPEED * TRAIL_MIN_SPEED)) {
            return;
        }
        if (motion.lengthSqr() > 1.0E-6) {
            pos = pos.add(motion.normalize().scale(-TRAIL_MOTION_SHIFT));
        }
        pos = pos.add(TRAIL_POSITION_OFFSET);
        addTrailPoint(pos);
    }

    @SuppressWarnings("null")
    private void addTrailPoint(Vec3 pos) {
        TrailPoint last = trailPoints.peekLast();
        float tex = last == null ? 0.0f : last.texcoord + (float) last.position.distanceTo(pos);
        trailPoints.addLast(new TrailPoint(pos, 0.0f, tex));
        trimTrail();
    }

    @SuppressWarnings("null")
    private void trimTrail() {
        for (TrailPoint p : trailPoints) {
            p.age += 1.0f;
        }
        while (!trailPoints.isEmpty() && trailPoints.peekFirst().age > TRAIL_MAX_AGE) {
            trailPoints.removeFirst();
        }
        while (trailPoints.size() > TRAIL_MAX_POINTS) {
            trailPoints.removeFirst();
        }
    }

    public Deque<TrailPoint> getTrailPoints() {
        return trailPoints;
    }

    public static int getTrailMaxAge() {
        return TRAIL_MAX_AGE;
    }

    public static final class TrailPoint {
        public final Vec3 position;
        public float age;
        public final float texcoord;

        public TrailPoint(Vec3 position, float age, float texcoord) {
            this.position = position;
            this.age = age;
            this.texcoord = texcoord;
        }
    }

    @SuppressWarnings("null")
    @Override
    protected void onHitEntity(@SuppressWarnings("null") EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        
        if (target == owner) return; // 不伤害自己
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.invulnerableTime = 0;
        }

        DamageSource source = this.damageSources().mobProjectile(this, (LivingEntity) owner);
        if (owner instanceof net.minecraft.world.entity.player.Player player && skillId != null) {
            long nowTick = this.level().getGameTime();
            SkillContext context = SkillContext.builder()
                .skillId(skillId)
                .tier("meowmere_symphony".equals(skillId) ? SkillContext.SkillTier.MAJOR : SkillContext.SkillTier.MINOR)
                .damageMultiplier("meowmere_symphony".equals(skillId) ? 0.8f : 1.0f)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 20);
        }
        target.hurt(source, this.damage);

        // 命中音效
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.MEOW.get(), SoundSource.PLAYERS, 0.6f, 1.0f + (this.random.nextFloat() - 0.5f) * 0.2f);
        }
        
        // 穿透逻辑
        if (pierceCount < 0) {
            return;
        }
        if (pierceCount > 0) {
            pierceCount--;
            return;
        }
        this.discard();
    }

    @SuppressWarnings("null")
    @Override
    protected void onHitBlock(@SuppressWarnings("null") BlockHitResult result) {
        // 反弹逻辑
        @SuppressWarnings("null")
        int bounces = this.entityData.get(BOUNCES);
        if (bounces >= MAX_BOUNCES) {
            this.discard();
            return;
        }

        this.entityData.set(BOUNCES, bounces + 1);
        
        // 播放反弹音效
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.MEOW.get(), SoundSource.NEUTRAL, 0.5f, 1.0f + (this.random.nextFloat() - 0.5f) * 0.2f);
        
        // 计算反弹向量
        Vec3 velocity = this.getDeltaMovement();
        @SuppressWarnings("null")
        Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
        
        // V_new = V_old - 2 * (V_old · N) * N
        // 简单的完全弹性碰撞公式，加上一点摩擦力
        @SuppressWarnings("null")
        double dot = velocity.dot(normal);
        @SuppressWarnings("null")
        Vec3 reflection = velocity.subtract(normal.scale(2 * dot)).scale(0.85); // 0.85 能量保持率
        
        this.setDeltaMovement(reflection); 
        
        // 稍微推开一点，防止卡在墙里
        this.setPos(this.position().add(reflection.normalize().scale(0.1)));
    }

    @SuppressWarnings("null")
    @Override
    public void addAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Bounces", this.entityData.get(BOUNCES));
        tag.putFloat("Damage", this.damage);
        tag.putInt("PierceCount", this.pierceCount);
    }

    @SuppressWarnings("null")
    @Override
    public void readAdditionalSaveData(@SuppressWarnings("null") CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("Bounces")) this.entityData.set(BOUNCES, tag.getInt("Bounces"));
        if(tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if(tag.contains("PierceCount")) this.pierceCount = tag.getInt("PierceCount");
    }
    
    // 渲染需要这些
    @SuppressWarnings("null")
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(@SuppressWarnings("null") net.minecraft.server.level.ServerEntity serverEntity) {
        return super.getAddEntityPacket(serverEntity); // ThrowableProjectile处理了基础的同步
    }
}
