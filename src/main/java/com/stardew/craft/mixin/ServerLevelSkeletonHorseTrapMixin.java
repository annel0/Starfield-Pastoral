package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelSkeletonHorseTrapMixin {

    @Redirect(
            method = "tickChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;",
                    ordinal = 0
            )
    )
    @SuppressWarnings("resource")
    private Entity stardewcraft$disableSkeletonHorseTrap(EntityType<?> entityType, Level level) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (ModDimensions.STARDEW_VALLEY.equals(self.dimension()) && entityType == EntityType.SKELETON_HORSE) {
            return null;
        }
        return entityType.create(level);
    }
}