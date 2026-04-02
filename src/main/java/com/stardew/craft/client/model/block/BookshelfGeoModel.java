package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.blockentity.BookshelfGeoBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("null")
public class BookshelfGeoModel extends GeoModel<BookshelfGeoBlockEntity> {
    private static final ResourceLocation MODEL_3_2 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/decor/bookshelf_3_2.geo.json");
    private static final ResourceLocation TEXTURE_3_2 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/deco/misc/common/bookshelf_3_2.png");
    private static final ResourceLocation MODEL_3_3 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/decor/bookshelf_3_3.geo.json");
    private static final ResourceLocation TEXTURE_3_3 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/deco/misc/common/bookshelf_3_3.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animations/block/decor/bookshelf_geo.animation.json");

    @Override
    public ResourceLocation getModelResource(BookshelfGeoBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        return state.is(ModBlocks.BOOKSHELF_TALL_2.get()) ? MODEL_3_3 : MODEL_3_2;
    }

    @Override
    public ResourceLocation getTextureResource(BookshelfGeoBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        return state.is(ModBlocks.BOOKSHELF_TALL_2.get()) ? TEXTURE_3_3 : TEXTURE_3_2;
    }

    @Override
    public ResourceLocation getAnimationResource(BookshelfGeoBlockEntity animatable) {
        return ANIMATION;
    }
}
