package com.stardew.craft.entity.junimo;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.damagesource.DamageSource;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

/**
 * Junimo entity for the Community Center bundle system.
 * Color is applied via tint layer in the renderer; the entity stores a packed RGB color.
 */
@SuppressWarnings("null")
public class JunimoEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(JunimoEntity.class, EntityDataSerializers.INT);
    /** What this Junimo is carrying: 0=nothing, 1=bundle, 2=star, 3=orange. */
    private static final EntityDataAccessor<Integer> DATA_HOLDING_TYPE =
            SynchedEntityData.defineId(JunimoEntity.class, EntityDataSerializers.INT);
    /** SDV parity: bundle color (packed RGB) carried by this Junimo. */
    private static final EntityDataAccessor<Integer> DATA_BUNDLE_COLOR =
            SynchedEntityData.defineId(JunimoEntity.class, EntityDataSerializers.INT);

    public static final int HOLDING_NONE = 0;
    public static final int HOLDING_BUNDLE = 1;
    public static final int HOLDING_STAR = 2;
    public static final int HOLDING_ORANGE = 3;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation HOLD_WALK = RawAnimation.begin().thenLoop("hold_walk");
    @SuppressWarnings("unused")
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlayAndHold("jump");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** Default Junimo color: lime green (same as area 0 in original). */
    private static final int DEFAULT_COLOR = 0x32CD32; // LimeGreen

    /** 脚本目标位置 (到达后执行 onArrival 动作) */
    @Nullable
    private BlockPos targetPos;
    /** 到达目标后的动作 */
    private Runnable onArrival;
    /** fadeOut 计时 (tick)，-1 表示不淡出 */
    private int fadeOutTicks = -1;
    /** fadeIn 计时 (tick) */
    private int fadeInTicks = 0;
    private static final int FADE_DURATION = 20; // 1秒
    /** 当前透明度 (0~1, 供渲染器使用) */
    private float alpha = 0f;
    /** 存活上限 tick，超时自动移除 (防止泄漏) */
    private int maxLifeTicks = 1200; // 60秒 (玩家可能在 BundleScreen 中停留较久)
    /** 如果 true，不会超时自动移除 (idle Junimos at note positions) */
    private boolean noTimeout = false;

    public JunimoEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
        this.setInvulnerable(true);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    /** 寻路卡住计数器——连续多 tick 导航完成但未到达目标时递增 */
    private int stuckTicks = 0;
    private static final int STUCK_TELEPORT_THRESHOLD = 60; // 3秒卡住后传送

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D);
    }

    // ── Synched Data ────────────────────────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, DEFAULT_COLOR);
        builder.define(DATA_HOLDING_TYPE, HOLDING_NONE);
        builder.define(DATA_BUNDLE_COLOR, 0x00FF00); // default Lime
    }

    public int getJunimoColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setJunimoColor(int rgb) {
        this.entityData.set(DATA_COLOR, rgb);
    }

    public boolean isHolding() {
        return this.entityData.get(DATA_HOLDING_TYPE) != HOLDING_NONE;
    }

    /** @deprecated use {@link #setHoldingType(int)} instead */
    @Deprecated
    public void setHolding(boolean holding) {
        this.entityData.set(DATA_HOLDING_TYPE, holding ? HOLDING_BUNDLE : HOLDING_NONE);
    }

    public int getHoldingType() {
        return this.entityData.get(DATA_HOLDING_TYPE);
    }

    public void setHoldingType(int type) {
        this.entityData.set(DATA_HOLDING_TYPE, type);
    }

    /** SDV parity: the color tint applied to the bundle item this Junimo carries. */
    public int getBundleColor() {
        return this.entityData.get(DATA_BUNDLE_COLOR);
    }

    public void setBundleColor(int rgb) {
        this.entityData.set(DATA_BUNDLE_COLOR, rgb);
    }

    /**
     * SDV parity: Bundle.getColorFromColorIndex(int color).
     * Maps bundle color index (0-6) to packed RGB.
     */
    public static int getColorFromBundleColorIndex(int colorIndex) {
        return switch (colorIndex) {
            case 0 -> 0x00FF00; // Lime
            case 1 -> 0xFF1493; // DeepPink
            case 2, 3 -> 0xFFA500; // Orange
            case 4 -> 0xFF0000; // Red
            case 5 -> 0xADD8E6; // LightBlue
            case 6 -> 0x00FFFF; // Cyan
            default -> 0x00FF00; // Lime
        };
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
        tag.putInt("HoldingType", getHoldingType());
        tag.putInt("BundleColor", getBundleColor());
        tag.putBoolean("Holding", isHolding());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("JunimoColor")) {
            setJunimoColor(tag.getInt("JunimoColor"));
        }
        if (tag.contains("HoldingType")) {
            setHoldingType(tag.getInt("HoldingType"));
        } else if (tag.contains("Holding")) {
            setHolding(tag.getBoolean("Holding"));
        }
        if (tag.contains("BundleColor")) {
            setBundleColor(tag.getInt("BundleColor"));
        }
    }

    // ── AI / Goals ──────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        // Junimos are script-driven — goals are set dynamically via setTarget()
    }

    // ── Scripted Movement ───────────────────────────────────────

    /**
     * 设置脚本目标：Junimo 将寻路到目标位置，到达后执行回调。
     */
    public void setTarget(@Nullable BlockPos pos, @Nullable Runnable arrival) {
        this.targetPos = pos;
        this.onArrival = arrival;
        if (pos != null) {
            this.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.5);
        }
    }

    /** 开始 fadeOut 动画，完成后自动移除实体 */
    public void startFadeOut() {
        this.fadeOutTicks = FADE_DURATION;
    }

    /** 设置为不超时 (idle Junimos at note positions — SDV resetSharedState parity) */
    public void setNoTimeout(boolean noTimeout) {
        this.noTimeout = noTimeout;
        if (noTimeout) {
            this.maxLifeTicks = Integer.MAX_VALUE;
        }
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public void tick() {
        super.tick();

        // fadeIn
        if (fadeInTicks < FADE_DURATION) {
            fadeInTicks++;
            alpha = (float) fadeInTicks / FADE_DURATION;
        }

        // fadeOut
        if (fadeOutTicks >= 0) {
            fadeOutTicks--;
            alpha = Math.max(0, (float) fadeOutTicks / FADE_DURATION);
            if (fadeOutTicks <= 0) {
                this.discard();
                return;
            }
        }

        // 超时自动移除 (idle Junimos exempt via noTimeout)
        if (!noTimeout) {
            maxLifeTicks--;
            if (maxLifeTicks <= 0) {
                this.discard();
                return;
            }
        }

        // 脚本目标到达检测
        if (targetPos != null) {
            double distSq = this.distanceToSqr(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
            if (distSq < 2.5) {
                // 到达目标
                stuckTicks = 0;
                targetPos = null;
                if (onArrival != null) {
                    Runnable cb = onArrival;
                    onArrival = null;
                    cb.run();
                }
            } else if (this.getNavigation().isDone()) {
                stuckTicks++;
                if (stuckTicks >= STUCK_TELEPORT_THRESHOLD) {
                    // 卡住太久——直接传送到目标附近
                    this.setPos(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                    stuckTicks = 0;
                } else {
                    // 重新尝试寻路
                    this.getNavigation().moveTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.5);
                }
            } else {
                stuckTicks = 0;
            }
        }
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
