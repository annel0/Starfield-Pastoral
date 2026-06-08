package com.stardew.craft.block.utility;

import com.stardew.craft.blockentity.CoffeeMakerBlockEntity;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.blockentity.UtilityDropHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CoffeeMakerBlock extends AbstractTwoBlockUtilityBlock<CoffeeMakerBlockEntity> {
    public CoffeeMakerBlock(Properties properties) {
        super(properties,
            "stardewcraft:block/utility/coffee_maker",
            "stardewcraft:block/utility/coffee_maker_extension",
            Direction.SOUTH);
    }

    @Override
    protected BlockEntityType<CoffeeMakerBlockEntity> blockEntityType() {
        return ModBlockEntities.COFFEE_MAKER.get();
    }

    @Override
    protected CoffeeMakerBlockEntity newMainBlockEntity(BlockPos pos, BlockState state) {
        return new CoffeeMakerBlockEntity(pos, state);
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state, CoffeeMakerBlockEntity blockEntity) {
        CoffeeMakerBlockEntity.serverTick(level, pos, state, blockEntity);
    }

    @Override
    protected boolean tryHarvest(Level level, BlockPos pos, Player player, CoffeeMakerBlockEntity blockEntity) {
        return UtilityDropHelper.tryHarvest(level, pos, player, blockEntity::isReady, blockEntity::harvestOne, UtilityDropHelper.LOW_MACHINE_VANILLA_XP);
    }
}
