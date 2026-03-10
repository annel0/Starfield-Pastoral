package com.stardew.craft.mixin;

import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Container.class)
public interface ContainerMixin {

    /**
     * @author StardewCraft
     * @reason 增加全局容器堆叠上限
     */
    @Overwrite
    default int getMaxStackSize() {
        return 999;
    }
}
