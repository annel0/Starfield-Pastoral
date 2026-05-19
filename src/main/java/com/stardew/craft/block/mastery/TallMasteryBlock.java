package com.stardew.craft.block.mastery;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("null")
public class TallMasteryBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private final VoxelShape[] mainShapes;
    private final VoxelShape[] extensionShapes;

    public enum Part implements StringRepresentable {
        MAIN("main"),
        EXTENSION("extension");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public TallMasteryBlock(Properties properties, String modelId, Direction baseFacing) {
        super(properties);
        this.mainShapes = ModelVoxelShapeCache.horizontalShapes(modelId, baseFacing);
        this.extensionShapes = ModelVoxelShapeCache.horizontalShapes(modelId + "_extension", baseFacing);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, Part.MAIN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(PART) == Part.EXTENSION) return List.of();
        return List.of(new ItemStack(asItem()));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getPartShape(state, mainShapes, extensionShapes);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    protected static VoxelShape getPartShape(BlockState state, VoxelShape[] mainShapes, VoxelShape[] extensionShapes) {
        int index = ModelVoxelShapeCache.horizontalIndex(state.getValue(FACING));
        return state.getValue(PART) == Part.EXTENSION ? extensionShapes[index] : mainShapes[index];
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos extensionPos = pos.above();
        if (!level.getWorldBorder().isWithinBounds(extensionPos)) return null;
        if (!level.getBlockState(extensionPos).canBeReplaced(context)) return null;
        return defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection())
            .setValue(PART, Part.MAIN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide || state.getValue(PART) != Part.MAIN) return;
        level.setBlock(pos.above(), state.setValue(PART, Part.EXTENSION), 3);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            Part part = state.getValue(PART);
            BlockPos otherPos = part == Part.MAIN ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.getBlock() == this && otherState.getValue(PART) != part) {
                level.removeBlock(otherPos, false);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(PART) == Part.EXTENSION && !player.isCreative()) {
            popResource(level, pos, new ItemStack(asItem()));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Part part = state.getValue(PART);
        BlockPos otherPos = part == Part.MAIN ? pos.above() : pos.below();
        if (neighborPos.equals(otherPos) && !neighborState.is(this)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public static BlockPos getMainPos(BlockPos pos, BlockState state) {
        return state.getValue(PART) == Part.EXTENSION ? pos.below() : pos;
    }
}