package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.utility.WoodenChestColorPalette;
import com.stardew.craft.blockentity.MineChestBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 矿井宝箱模型 — 复用 WoodenChest 的模型/动画/材质资源。
 */
@SuppressWarnings("null")
public class MineChestGeoModel extends GeoModel<MineChestBlockEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/utility/wooden_chest_default.geo.json");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animations/block/utility/wooden_chest_default.animation.json");
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/utility/wooden_chest_default.png");
    private static final ResourceLocation[] COLOR_TEXTURES = buildColorTextures();

    private static ResourceLocation[] buildColorTextures() {
        ResourceLocation[] textures = new ResourceLocation[WoodenChestColorPalette.size()];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = ResourceLocation.fromNamespaceAndPath(
                StardewCraft.MODID,
                String.format("textures/block/utility/wooden_chest_color_%02d.png", i)
            );
        }
        return textures;
    }

    @Override
    public ResourceLocation getModelResource(MineChestBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MineChestBlockEntity animatable) {
        int index = WoodenChestColorPalette.clampIndex(animatable.getColorSelection());
        if (index < 0) {
            return DEFAULT_TEXTURE;
        }
        return COLOR_TEXTURES[index];
    }

    @Override
    public ResourceLocation getAnimationResource(MineChestBlockEntity animatable) {
        return ANIMATION;
    }
}
