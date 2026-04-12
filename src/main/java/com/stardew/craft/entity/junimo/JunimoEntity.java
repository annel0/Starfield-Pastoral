package com.stardew.craft.entity.junimo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Junimo entity for the Community Center bundle system.
 * Color is applied via tint layer in the renderer; the entity stores a packed RGB color.
 */
@SuppressWarnings("null")
public class JunimoEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(JunimoEntity.class, EntityDataSerializers.INT);
    /** Whether this Junimo is carrying an item (star or bundle). */
    private static final EntityDataAccessor<Boolean> DATA_HOLDING =
            SynchedEntityData.defineId(JunimoEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation HOLD_WALK = RawAnimation.begin().thenLoop("hold_walk");
    @SuppressWarnings("unused")
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlayAndHold("jump");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** Default Junimo color: lime green (same as area 0 in original). */
    private static final int DEFAULT_COLOR = 0x32CD32; // LimeGreen

    public JunimoEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    // ── Synched Data ────────────────────────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, DEFAULT_COLOR);
        builder.define(DATA_HOLDING, false);
    }

    public int getJunimoColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setJunimoColor(int rgb) {
        this.entityData.set(DATA_COLOR, rgb);
    }

    public boolean isHolding() {
        return this.entityData.get(DATA_HOLDING);
    }

    public void setHolding(boolean holding) {
        this.entityData.set(DATA_HOLDING, holding);
    }

    /**
     * Returns the color as an array of [r, g, b] floats in 0..1 range,
     * suitable for tinting render calls.
     */
    public float[] getColorComponents() {
        int c = getJunimoColor();
        return new float[]{
                ((c >> 16) & 0xFF) / 255f,
                ((c >> 8) & 0xFF) / 255f,
                (c & 0xFF) / 255f
        };
    }

    // ── Persistence ─────────────────────────────────────────────

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("JunimoColor", getJunimoColor());
        tag.putBoolean("Holding", isHolding());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("JunimoColor")) {
            setJunimoColor(tag.getInt("JunimoColor"));
        }
        if (tag.contains("Holding")) {
            setHolding(tag.getBoolean("Holding"));
        }
    }

    // ── AI / Goals ──────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        // Junimos are scripted; no autonomous AI goals for now.
    }

    // ── GeckoLib Animation ──────────────────────────────────────

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
                if (isHolding()) {
                    return state.setAndContinue(HOLD_WALK);
                }
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
