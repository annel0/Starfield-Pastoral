package com.stardew.craft.block.portal;

import com.stardew.craft.blockentity.PortalTriggerBlockEntity;
import com.stardew.craft.event.InteriorPortalInteractionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 隐形传送触发方块 — 替代 vanilla Interaction 实体用于室内外传送。
 * <p>
 * 特性：
 * - 完全隐形（无模型、无贴图）
 * - 无碰撞箱和选择框
 * - 不可破坏
 * - 右键触发传送（通过 BlockEntity 存储的目标 ID）
 */
@SuppressWarnings("null")
public class PortalTriggerBlock extends Block implements EntityBlock {

    public PortalTriggerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 返回完整方块形状，使玩家准星能瞄准并右键交互
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 无碰撞箱，玩家可穿过
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PortalTriggerBlockEntity portalBE)) return InteractionResult.PASS;

        String targetId = portalBE.getTargetId();
        if (targetId == null || targetId.isEmpty()) return InteractionResult.PASS;

        InteriorPortalInteractionEvents.handlePortalInteraction(sp, targetId);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalTriggerBlockEntity(pos, state);
    }
}
