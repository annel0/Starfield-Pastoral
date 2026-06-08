package com.stardew.craft.item;

import com.stardew.craft.block.utility.AbstractTwoBlockUtilityBlock;
import com.stardew.craft.blockentity.FairyDustAcceleratable;
import com.stardew.craft.blockentity.TapperBlockEntity;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fairy dust item: accelerates compatible machines to finish soon.
 */
public class FairyDustItem extends SimpleStardewItem {
    public FairyDustItem(int sellPrice, Properties properties) {
        super("stardewcraft.type.craftable", sellPrice, properties);
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResult useOn(@SuppressWarnings("null") UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos targetPos = resolveMainPos(clickedPos, clickedState);
        BlockState targetState = level.getBlockState(targetPos);
        BlockEntity be = level.getBlockEntity(targetPos);

        if (be instanceof TapperBlockEntity tapper) {
            tapper.ensureCycleStarted(targetState);
        }
        if (!(be instanceof FairyDustAcceleratable acceleratable) || !acceleratable.canApplyFairyDust()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            if (!acceleratable.applyFairyDust()) {
                return InteractionResult.PASS;
            }

            if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }

            if (level instanceof ServerLevel serverLevel) {
                double x = targetPos.getX() + 0.5;
                double y = targetPos.getY() + 0.8;
                double z = targetPos.getZ() + 0.5;
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 12, 0.3, 0.2, 0.3, 0.02);
                serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 10, 0.25, 0.15, 0.25, 0.02);
            }

            level.playSound(null, targetPos, ModSounds.YOBA.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static BlockPos resolveMainPos(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof AbstractTwoBlockUtilityBlock<?>) {
            return AbstractTwoBlockUtilityBlock.getMainPos(pos, state);
        }
        return pos;
    }
}
