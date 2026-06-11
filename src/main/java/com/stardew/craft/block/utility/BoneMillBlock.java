package com.stardew.craft.block.utility;

import com.stardew.craft.blockentity.BoneMillBlockEntity;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.blockentity.UtilityDropHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BoneMillBlock extends AbstractTwoBlockUtilityBlock<BoneMillBlockEntity> {
    public BoneMillBlock(Properties properties) {
        super(properties,
            "stardewcraft:block/utility/bone_mill",
            "stardewcraft:block/utility/bone_mill_extension",
            Direction.SOUTH);
    }

    @Override
    protected BlockEntityType<BoneMillBlockEntity> blockEntityType() {
        return ModBlockEntities.BONE_MILL.get();
    }

    @Override
    protected BoneMillBlockEntity newMainBlockEntity(BlockPos pos, BlockState state) {
        return new BoneMillBlockEntity(pos, state);
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state, BoneMillBlockEntity blockEntity) {
        BoneMillBlockEntity.serverTick(level, pos, state, blockEntity);
    }

    @Override
    protected boolean tryHarvest(Level level, BlockPos pos, Player player, BoneMillBlockEntity blockEntity) {
        return UtilityDropHelper.tryHarvest(level, pos, player,
            blockEntity::isReady, blockEntity::harvestOne, UtilityDropHelper.STANDARD_MACHINE_VANILLA_XP);
    }

    @Override
    protected boolean tryInsert(ItemStack stack, Level level, BlockPos pos, Player player, BoneMillBlockEntity blockEntity) {
        return blockEntity.tryInsert(stack, player);
    }
}
