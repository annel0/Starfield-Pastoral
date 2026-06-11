package com.stardew.craft.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class StardewCraftMixinPlugin implements IMixinConfigPlugin {
    private static final String PURPLE_SHORTS_BOBBER_MIXIN =
            "com.stardew.craft.mixin.FishingHookRendererPurpleShortsBobberMixin";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (PURPLE_SHORTS_BOBBER_MIXIN.equals(mixinClassName) && isHybridAquaticLoaded()) {
            return false;
        }
        return true;
    }

    private static boolean isHybridAquaticLoaded() {
        try {
            LoadingModList modList = LoadingModList.get();
            return modList != null && modList.getModFileById("hybrid_aquatic") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
