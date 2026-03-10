package com.stardew.craft.combat.skill;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class BoneFractureTracker {

    private static final String TAG_END_TICK = "stardewcraft_bone_fracture_until";
    private static final String TAG_LAST_PARTICLE = "stardewcraft_bone_fracture_particle";
    private static final int PARTICLE_INTERVAL = 6;

    private BoneFractureTracker() {}

    public static void apply(ServerLevel level, LivingEntity target, long nowTick, int durationTicks) {
        CompoundTag tag = target.getPersistentData();
        tag.putLong(TAG_END_TICK, nowTick + durationTicks);
        tag.putLong(TAG_LAST_PARTICLE, nowTick - PARTICLE_INTERVAL);
        spawnParticles(level, target, 6);
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
        if (nowTick >= endTick) {
            tag.remove(TAG_END_TICK);
            tag.remove(TAG_LAST_PARTICLE);
            return;
        }

        long last = tag.getLong(TAG_LAST_PARTICLE);
        if (nowTick - last < PARTICLE_INTERVAL) {
            return;
        }
        tag.putLong(TAG_LAST_PARTICLE, nowTick);

        if (entity.level() instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel, entity, 2);
        }
    }

    @SuppressWarnings("null")
    private static void spawnParticles(ServerLevel level, LivingEntity target, int count) {
        ItemParticleOption bone = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BONE));
        level.sendParticles(
            bone,
            target.getX(),
            target.getY() + target.getBbHeight() * 0.6,
            target.getZ(),
            count,
            0.2, 0.25, 0.2,
            0.02
        );
    }
}