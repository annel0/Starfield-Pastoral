package com.stardew.craft.block.decor;

import com.stardew.craft.blockentity.PillarGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("null")
public class PillarGeoDecorBlock extends MapDecorStaticBlock implements EntityBlock {

    public PillarGeoDecorBlock(Properties properties, String modelId) {
        super(properties, modelId);
    }

    @Override
    protected Set<CellOffset> localOccupiedOffsets() {
        Set<CellOffset> cells = new LinkedHashSet<>();
        cells.add(new CellOffset(0, 0, 0));
        cells.add(new CellOffset(0, 1, 0));
        cells.add(new CellOffset(0, 2, 0));
        return cells;
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (state.getValue(PART) != Part.MAIN) {
            return null;
        }
        return new PillarGeoBlockEntity(pos, state);
    }
}
