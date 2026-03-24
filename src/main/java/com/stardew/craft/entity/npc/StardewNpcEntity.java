package com.stardew.craft.entity.npc;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.runtime.NpcInteractionService;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@SuppressWarnings("null")
public class StardewNpcEntity extends PathfinderMob implements GeoEntity {
    private static final int INVALID_ID_GRACE_TICKS = 40;
    private static final EntityDataAccessor<String> DATA_NPC_ID = SynchedEntityData.defineId(StardewNpcEntity.class, EntityDataSerializers.STRING);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public StardewNpcEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
        // Keep AI loop enabled so MC navigation/pathfinder can run.
        this.setNoAi(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.20D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.STEP_HEIGHT, 0.6D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_NPC_ID, "");
    }

    @Override
    protected void registerGoals() {
        // Intentionally empty: NPC movement is centrally managed by runtime services.
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("NpcId", getNpcId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("NpcId")) {
            setNpcId(tag.getString("NpcId"));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.sidedSuccess(true);
        }
        return NpcInteractionService.onInteract(player, this, hand);
    }

    public String getNpcId() {
        return this.entityData.get(DATA_NPC_ID);
    }

    public void setNpcId(String npcId) {
        this.entityData.set(DATA_NPC_ID, npcId == null ? "" : npcId.toLowerCase());
    }

    public boolean hasValidNpcId() {
        String npcId = getNpcId();
        return npcId != null && !npcId.isBlank() && NpcDataRegistry.capabilities().containsKey(npcId);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (!hasValidNpcId()) {
                if (this.tickCount >= INVALID_ID_GRACE_TICKS) {
                    StardewCraft.LOGGER.warn(
                        "Discarding StardewNpcEntity with invalid npcId='{}' at tickCount={} pos=({}, {}, {})",
                        getNpcId(),
                        this.tickCount,
                        this.getX(),
                        this.getY(),
                        this.getZ()
                    );
                    this.discard();
                    return;
                }
            } else {
                if (this.tickCount >= 10 && !com.stardew.craft.npc.runtime.NpcSpawnManager.isOfficialInstance(this)) {
                    StardewCraft.LOGGER.warn(
                        "Self-destructing strictly forbidden duplicate StardewNpcEntity id='{}' uuid={}",
                        getNpcId(),
                        this.getUUID()
                    );
                    this.discard();
                    return;
                }
            }
        }
        super.tick();
    }

    public boolean isPathingEnabled() {
        NpcCapabilityProfile profile = NpcDataRegistry.capabilities().get(getNpcId());
        return profile != null && profile.canRunPathing();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5D && isPathingEnabled()) {
                state.setAndContinue(WALK);
                return PlayState.CONTINUE;
            }
            state.setAndContinue(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
}
