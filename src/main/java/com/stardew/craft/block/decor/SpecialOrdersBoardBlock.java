package com.stardew.craft.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;

/**
 * Fixed town special-orders board. The actual order UI is wired in a later pass.
 */
@SuppressWarnings("null")
public class SpecialOrdersBoardBlock extends MapDecorStaticBlock {

    public SpecialOrdersBoardBlock(Properties properties, String modelId) {
        super(properties, modelId);
    }

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state,
                                               @Nonnull Level level,
                                               @Nonnull BlockPos pos,
                                               @Nonnull Player player,
                                               @Nonnull BlockHitResult hit) {
        BlockPos mainPos = findMainPos(level, pos, state);
        if (mainPos == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("stardewcraft.special_orders.board.placeholder"), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
