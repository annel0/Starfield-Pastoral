package com.stardew.craft.block.decor;

import com.stardew.craft.blockentity.BookshelfGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("null")
public class BookshelfGeoDecorBlock extends MapDecorStaticBlock implements EntityBlock {

    // Geo bounds:  X[-8,24] Y[0,33/48] Z[-2,8]
    // MC pixels (NORTH): X[0,32] Y[0,33/48] Z[0,10]
    // Per-cell collision: full width, full height, 10px deep from the wall face.
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 16, 10);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 6, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST  = Block.box(6, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST  = Block.box(0, 0, 0, 10, 16, 16);

    private final int heightInBlocks;

    public BookshelfGeoDecorBlock(Properties properties, String modelId) {
        super(properties, modelId);
        this.heightInBlocks = modelId.contains("3_3") ? 3 : 2;
    }

    @Override
    protected Set<CellOffset> localOccupiedOffsets() {
        Set<CellOffset> cells = new LinkedHashSet<>();
        // Anchor MAIN on the right half of the 2-wide bookshelf, so collision
        // aligns with rendered geo and occupies one block to the left.
        for (int x = -1; x <= 0; x++) {
            for (int y = 0; y < heightInBlocks; y++) {
                cells.add(new CellOffset(x, y, 0));
            }
        }
        return cells;
    }

    private static VoxelShape shapeForFacing(Direction facing) {
        return switch (facing) {
            // Visual model and cached shape basis differ by 180 degrees.
            // Map facing to opposite side so collision aligns with rendered geo.
            case SOUTH -> SHAPE_NORTH;
            case EAST  -> SHAPE_WEST;
            case WEST  -> SHAPE_EAST;
            default    -> SHAPE_SOUTH;
        };
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level,
                               @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shapeForFacing(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level,
                                        @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shapeForFacing(state.getValue(FACING));
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
        return new BookshelfGeoBlockEntity(pos, state);
    }
}
