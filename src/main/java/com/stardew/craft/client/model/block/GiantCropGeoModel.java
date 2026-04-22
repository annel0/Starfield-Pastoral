package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.crop.giant.GiantCauliflowerBlock;
import com.stardew.craft.block.crop.giant.GiantMelonBlock;
import com.stardew.craft.block.crop.giant.GiantPowdermelonBlock;
import com.stardew.craft.block.crop.giant.GiantPumpkinBlock;
import com.stardew.craft.blockentity.GiantCropBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.model.GeoModel;

public class GiantCropGeoModel extends GeoModel<GiantCropBlockEntity> {

    private static final ResourceLocation MODEL_CAULI = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/crop/giant/cauliflower.geo.json");
    private static final ResourceLocation MODEL_MELON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/crop/giant/melon.geo.json");
    private static final ResourceLocation MODEL_PUMPKIN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/crop/giant/pumpkin.geo.json");
    private static final ResourceLocation MODEL_POWDERMELON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/crop/giant/powdermelon.geo.json");

    private static final ResourceLocation TEX_CAULI = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/crop/giant/cauliflower.png");
    private static final ResourceLocation TEX_MELON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/crop/giant/melon.png");
    private static final ResourceLocation TEX_PUMPKIN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/crop/giant/pumpkin.png");
    private static final ResourceLocation TEX_POWDERMELON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/crop/giant/powdermelon.png");

    @Override
    public ResourceLocation getModelResource(GiantCropBlockEntity be) {
        Block b = be.getBlockState().getBlock();
        if (b instanceof GiantCauliflowerBlock) return MODEL_CAULI;
        if (b instanceof GiantMelonBlock) return MODEL_MELON;
        if (b instanceof GiantPumpkinBlock) return MODEL_PUMPKIN;
        if (b instanceof GiantPowdermelonBlock) return MODEL_POWDERMELON;
        return MODEL_CAULI;
    }

    @Override
    public ResourceLocation getTextureResource(GiantCropBlockEntity be) {
        Block b = be.getBlockState().getBlock();
        if (b instanceof GiantCauliflowerBlock) return TEX_CAULI;
        if (b instanceof GiantMelonBlock) return TEX_MELON;
        if (b instanceof GiantPumpkinBlock) return TEX_PUMPKIN;
        if (b instanceof GiantPowdermelonBlock) return TEX_POWDERMELON;
        return TEX_CAULI;
    }

    @Override
    public ResourceLocation getAnimationResource(GiantCropBlockEntity be) {
        return null;
    }
}
