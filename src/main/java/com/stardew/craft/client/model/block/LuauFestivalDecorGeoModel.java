package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.blockentity.LuauFestivalDecorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LuauFestivalDecorGeoModel extends GeoModel<LuauFestivalDecorBlockEntity> {
    private static final ResourceLocation SOUP_POT_MODEL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/festival/luau_soup_pot.geo.json");
    private static final ResourceLocation SOUP_POT_TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/festival/luau_soup_pot.png");
    private static final ResourceLocation TOTEM_MODEL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/festival/luau_totem.geo.json");
    private static final ResourceLocation TOTEM_TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/festival/luau_totem.png");
    private static final ResourceLocation WINTER_STAR_TREE_MODEL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/festival/winter_star_tree.geo.json");
    private static final ResourceLocation WINTER_STAR_TREE_TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/festival/winter_star_tree.png");

    @Override
    public ResourceLocation getModelResource(LuauFestivalDecorBlockEntity animatable) {
        if (animatable.getBlockState().is(ModBlocks.WINTER_STAR_TREE.get())) {
            return WINTER_STAR_TREE_MODEL;
        }
        if (animatable.getBlockState().is(ModBlocks.LUAU_TOTEM.get())) {
            return TOTEM_MODEL;
        }
        return SOUP_POT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LuauFestivalDecorBlockEntity animatable) {
        if (animatable.getBlockState().is(ModBlocks.WINTER_STAR_TREE.get())) {
            return WINTER_STAR_TREE_TEXTURE;
        }
        if (animatable.getBlockState().is(ModBlocks.LUAU_TOTEM.get())) {
            return TOTEM_TEXTURE;
        }
        return SOUP_POT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LuauFestivalDecorBlockEntity animatable) {
        return null;
    }
}
