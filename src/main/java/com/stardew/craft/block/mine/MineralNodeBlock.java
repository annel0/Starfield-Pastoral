package com.stardew.craft.block.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * 直接采集矿物节点：空手右键可拾取，左键可正常破坏。
 */
public class MineralNodeBlock extends Block {
    @SuppressWarnings("null")
    public MineralNodeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state,
                                               @Nonnull Level level,
                                               @Nonnull BlockPos pos,
                                               @Nonnull Player player,
                                               @Nonnull BlockHitResult hitResult) {
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(Objects.requireNonNull(this.asItem()));
            if (!player.addItem(stack)) {
                popResource(level, pos, stack);
            }
            level.setBlock(pos, Objects.requireNonNull(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()), Block.UPDATE_ALL);
            level.playSound(null, pos, Objects.requireNonNull(SoundEvents.ITEM_PICKUP), SoundSource.PLAYERS, 0.2F, 1.0F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
