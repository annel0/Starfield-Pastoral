package com.stardew.craft.integration.jade;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.FertilizerType;
import com.stardew.craft.client.ClientFertilizerCache;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.manager.FertilizerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * 作物肥料信息Jade提供器
 * 显示作物下方耕地的肥料类型
 */
public enum CropFertilizerJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "crop_fertilizer");
    private static final String DATA_CHECKED = "stardewcraft_fertilizer_checked";
    private static final String DATA_TYPE = "stardewcraft_fertilizer";

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!(accessor.getBlockState().getBlock() instanceof CropBlock)
                && !(accessor.getBlockState().getBlock() instanceof com.stardew.craft.block.crop.StardewCropBlock)) {
            return;
        }
        if (com.stardew.craft.block.crop.StardewCropBlock.isDecorativeFlowerState(accessor.getBlockState())) {
            return;
        }

        BlockPos farmPos = resolveCropRootPos(accessor).below();
        if (!(accessor.getLevel().getBlockState(farmPos).getBlock() instanceof FarmBlock)) {
            return;
        }
        tag.putBoolean(DATA_CHECKED, true);
        FertilizerType type = FertilizerManager.get(level).getFertilizer(level, farmPos);
        if (type != null) {
            tag.putString(DATA_TYPE, type.getSerializedName());
        }
    }

    private static BlockPos resolveCropRootPos(BlockAccessor accessor) {
        BlockPos pos = accessor.getPosition();
        BlockState state = accessor.getBlockState();
        if (!state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            return pos;
        }
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER) {
            return pos;
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = accessor.getLevel().getBlockState(belowPos);
        if (belowState.getBlock() == state.getBlock()
                && belowState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && belowState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            return belowPos;
        }
        return pos;
    }

    @SuppressWarnings("null")
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockState().getBlock() instanceof CropBlock)
                && !(accessor.getBlockState().getBlock() instanceof com.stardew.craft.block.crop.StardewCropBlock)) {
            return;
        }

        // 玩家放置的花不显示肥料信息
        BlockState viewState = accessor.getBlockState();
        if (com.stardew.craft.block.crop.StardewCropBlock.isDecorativeFlowerState(viewState)) {
            return;
        }

        // 检查作物下方的耕地
        BlockPos farmPos = resolveCropRootPos(accessor).below();
        if (!(accessor.getLevel().getBlockState(farmPos).getBlock() instanceof FarmBlock)) {
            return;
        }
        FertilizerType type = getServerFertilizer(accessor.getServerData());
        if (type != null) {
            ClientFertilizerCache.setFertilizer(accessor.getLevel(), farmPos, type);
        } else if (accessor.getServerData().getBoolean(DATA_CHECKED)) {
            ClientFertilizerCache.removeFertilizer(accessor.getLevel(), farmPos);
        } else {
            type = ClientFertilizerCache.getFertilizer(accessor.getLevel(), farmPos);
        }

        if (type != null) {
            Item fertilizerItem = getFertilizerItem(type);
            if (fertilizerItem != null) {
                ItemStack stack = new ItemStack(fertilizerItem);
                tooltip.add(Component.translatable("stardewcraft.jade.fertilizer", stack.getHoverName())
                        .withStyle(net.minecraft.ChatFormatting.AQUA));
            }
        }
    }

    private FertilizerType getServerFertilizer(CompoundTag data) {
        if (!data.contains(DATA_TYPE)) {
            return null;
        }
        try {
            return FertilizerType.valueOf(data.getString(DATA_TYPE).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private Item getFertilizerItem(FertilizerType type) {
        return switch (type) {
            case BASIC_FERTILIZER -> ModItems.BASIC_FERTILIZER.get();
            case QUALITY_FERTILIZER -> ModItems.QUALITY_FERTILIZER.get();
            case DELUXE_FERTILIZER -> ModItems.DELUXE_FERTILIZER.get();
            case BASIC_RETAINING_SOIL -> ModItems.BASIC_RETAINING_SOIL.get();
            case QUALITY_RETAINING_SOIL -> ModItems.QUALITY_RETAINING_SOIL.get();
            case DELUXE_RETAINING_SOIL -> ModItems.DELUXE_RETAINING_SOIL.get();
            case SPEED_GRO -> ModItems.SPEED_GRO.get();
            case DELUXE_SPEED_GRO -> ModItems.DELUXE_SPEED_GRO.get();
            case HYPER_SPEED_GRO -> ModItems.HYPER_SPEED_GRO.get();
        };
    }
}
