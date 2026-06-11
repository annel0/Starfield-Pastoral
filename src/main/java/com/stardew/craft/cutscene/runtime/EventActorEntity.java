package com.stardew.craft.cutscene.runtime;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Lightweight client-only actor entity for cutscene events.
 * Reuses NPC GeckoLib models based on npcId.
 * Has no AI, no collision, no server-side logic.
 */
public class EventActorEntity extends Mob implements GeoEntity {

    public static final EntityDataAccessor<String> DATA_NPC_ID =
            SynchedEntityData.defineId(EventActorEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> DATA_IS_WALKING =
            SynchedEntityData.defineId(EventActorEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** Custom animation override (set by animate command). */
    private RawAnimation customAnimation = null;
    @SuppressWarnings("unused")
    private boolean customAnimationLoop = false;

    public EventActorEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(@javax.annotation.Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_NPC_ID, "");
        builder.define(DATA_IS_WALKING, false);
    }

    public String getNpcId() {
        return this.entityData.get(DATA_NPC_ID);
    }

    public void setNpcId(String id) {
        this.entityData.set(DATA_NPC_ID, id);
    }

    public boolean isWalking() {
        return this.entityData.get(DATA_IS_WALKING);
    }

    public void setWalking(boolean walking) {
        this.entityData.set(DATA_IS_WALKING, walking);
    }

    public void setCustomAnimation(String animName, boolean loop) {
        if (loop) {
            this.customAnimation = RawAnimation.begin().thenLoop(animName);
        } else {
            this.customAnimation = RawAnimation.begin().thenPlay(animName);
        }
        this.customAnimationLoop = loop;
    }

    public void clearCustomAnimation() {
        this.customAnimation = null;
    }

    // ─── GeckoLib ───

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if (customAnimation != null) {
                state.setAndContinue(customAnimation);
                return PlayState.CONTINUE;
            }
            if (isWalking()) {
                state.setAndContinue(WALK);
            } else {
                state.setAndContinue(IDLE);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ─── Override to be inert ───

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean isInvulnerableTo(@javax.annotation.Nonnull DamageSource source) { return true; }

    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public boolean canBeLeashed() { return false; }

    @Override
    public Component getName() {
        String npcId = getNpcId();
        if (npcId != null && !npcId.isEmpty()) {
            return Component.translatable("entity.stardewcraft.npc." + npcId);
        }
        return super.getName();
    }

    @Override
    public InteractionResult mobInteract(@javax.annotation.Nonnull net.minecraft.world.entity.player.Player player,
                                         @javax.annotation.Nonnull InteractionHand hand) {
        if (this.level().isClientSide || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }
}
