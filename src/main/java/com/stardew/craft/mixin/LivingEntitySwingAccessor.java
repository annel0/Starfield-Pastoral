package com.stardew.craft.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntitySwingAccessor {
    @Accessor("swingTime")
    void stardewcraft$setSwingTime(int swingTime);

    @Accessor("swinging")
    void stardewcraft$setSwinging(boolean swinging);
}
