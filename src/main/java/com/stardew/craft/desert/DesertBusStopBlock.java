package com.stardew.craft.desert;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 沙漠公交站牌。
 * <p>
 * 右键交互统一委托给 {@link DesertBusService}，弹出确认对话框后执行完整的
 * 扣钱 + 黑屏 + 音效 + 传送序列。
 */
public class DesertBusStopBlock extends Block {

    public DesertBusStopBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("null")
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        DesertBusService.beginBusRide((ServerPlayer) player);
        return InteractionResult.CONSUME;
    }
}

