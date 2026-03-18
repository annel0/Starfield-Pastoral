package com.stardew.craft.block.utility;

import com.stardew.craft.blockentity.DecorAnchorBlockEntity;
import com.stardew.craft.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import com.stardew.craft.blockentity.DecorAnchorBlockEntity;

import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("null")
public class DecorAnchorBlock extends Block implements EntityBlock {
    public static final IntegerProperty STYLE = IntegerProperty.create("style", 0, 24);

    public DecorAnchorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STYLE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STYLE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecorAnchorBlockEntity(pos, state);
    }



    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof DecorAnchorBlockEntity be)) {
            return InteractionResult.PASS;
        }
        PacketDistributor.sendToPlayer(serverPlayer, new com.stardew.craft.network.payload.OpenDecorAnchorEditorPayload(
            pos,
            be.getStyleId(),
            be.getOffsetX(),
            be.getOffsetY(),
            be.getOffsetZ(),
            be.getRotX(),
            be.getRotY(),
            be.getRotZ(),
            be.getScaleX(),
            be.getScaleY(),
            be.getScaleZ()
        ));
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof DecorAnchorBlockEntity be) {
            float dx = be.getOffsetX();
            float dy = be.getOffsetY();
            float dz = be.getOffsetZ();
            float sx = be.getScaleX();
            float sy = be.getScaleY();
            float sz = be.getScaleZ();
            double hw = 8.0 * sx;
            double hh = 8.0 * sy;
            double hd = 8.0 * sz;
            return Block.box(8.0 - hw, 0, 8.0 - hd, 8.0 + hw, hh * 2.0, 8.0 + hd).move(dx, dy, dz);
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }

}