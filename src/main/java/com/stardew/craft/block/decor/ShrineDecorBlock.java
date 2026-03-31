package com.stardew.craft.block.decor;

import com.stardew.craft.blockentity.ShrineBlockEntity;
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
public class ShrineDecorBlock extends MapDecorStaticBlock implements EntityBlock {

    // NORTH facing world-pixel bounds (from GeckoLib coordinate conversion):
    //   X[-30,46] Y[0,34] Z[-17,16]
    // Practical bounding box: 5W(-2..2) × 2H(0..1) × 2D(-1..0)
    // Each cell uses a full block shape for simplicity.

    public ShrineDecorBlock(Properties properties, String modelId) {
        super(properties, modelId);
    }

    // Geo bounds:  X[-24,24] Y[0,34] Z[-8,25]
    // MC pixels (NORTH): X[-16,32] Y[0,34] Z[-17,16]
    // Cell coverage: 3W(x=-1..1) × 2H(y=0..1) × 2D(z=-1..0)
    @Override
    protected Set<CellOffset> localOccupiedOffsets() {
        Set<CellOffset> cells = new LinkedHashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 0; z++) {
                    cells.add(new CellOffset(x, y, z));
                }
            }
        }
        return cells;
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return getShrineShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return getShrineShape(state);
    }

    private VoxelShape getShrineShape(BlockState state) {
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
        return new ShrineBlockEntity(pos, state);
    }
}
