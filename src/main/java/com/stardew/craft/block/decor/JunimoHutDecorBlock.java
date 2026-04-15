package com.stardew.craft.block.decor;

import com.stardew.craft.blockentity.JunimoHutDecorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("null")
public class JunimoHutDecorBlock extends MapDecorStaticBlock implements EntityBlock {

    // Solid body MC NORTH pixels: X[-14.5, 30.5] Y[0.5, 48.6] Z[-9.8, 27.2]
    // Cell coverage: X[-1,1] Y[0,2] Z[-1,1] = 3×3×3 = 27 cells

    public JunimoHutDecorBlock(Properties properties, String modelId) {
        // AABB box collision wrapping the solid hut body (pixels, NORTH facing)
        super(properties, modelId, -15.0, 0.0, -10.0, 31.0, 48.0, 28.0);
    }

    @Override
    protected Set<CellOffset> localOccupiedOffsets() {
        Set<CellOffset> cells = new LinkedHashSet<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    cells.add(new CellOffset(x, y, z));
                }
            }
        }
        return cells;
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
        return new JunimoHutDecorBlockEntity(pos, state);
    }
}
