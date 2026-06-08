package com.stardew.craft.block.utility;

import com.stardew.craft.blockentity.GeodeCrusherBlockEntity;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.blockentity.UtilityDropHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GeodeCrusherBlock extends AbstractTwoBlockUtilityBlock<GeodeCrusherBlockEntity> {
    public GeodeCrusherBlock(Properties properties) {
        super(properties,
            "stardewcraft:block/utility/geode_crusher",
            "stardewcraft:block/utility/geode_crusher_extension",
            Direction.EAST);
    }

    @Override
    protected BlockEntityType<GeodeCrusherBlockEntity> blockEntityType() {
        return ModBlockEntities.GEODE_CRUSHER.get();
    }

    @Override
    protected GeodeCrusherBlockEntity newMainBlockEntity(BlockPos pos, BlockState state) {
        return new GeodeCrusherBlockEntity(pos, state);
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state, GeodeCrusherBlockEntity blockEntity) {
        GeodeCrusherBlockEntity.serverTick(level, pos, state, blockEntity);
    }

    @Override
    protected void clientTick(Level level, BlockPos pos, BlockState state, GeodeCrusherBlockEntity blockEntity) {
        GeodeCrusherBlockEntity.clientTick(level, pos, state, blockEntity);
    }

    @Override
    protected boolean tryHarvest(Level level, BlockPos pos, Player player, GeodeCrusherBlockEntity blockEntity) {
        return UtilityDropHelper.tryHarvest(level, pos, player, blockEntity::isReady, blockEntity::harvestOne, UtilityDropHelper.STANDARD_MACHINE_VANILLA_XP);
    }

    @Override
    protected boolean tryInsert(ItemStack stack, Level level, BlockPos pos, Player player, GeodeCrusherBlockEntity blockEntity) {
        return blockEntity.tryInsert(stack, player);
    }

}
