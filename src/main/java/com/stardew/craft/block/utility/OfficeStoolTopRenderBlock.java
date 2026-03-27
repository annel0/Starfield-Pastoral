package com.stardew.craft.block.utility;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

@SuppressWarnings("null")
public class OfficeStoolTopRenderBlock extends Block {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, WoodenChestColorPalette.size() - 1);

    public OfficeStoolTopRenderBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLOR, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }
}
