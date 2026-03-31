package com.stardew.craft.block.decor;

import java.util.Set;

/**
 * A flat decor block that only occupies the MAIN cell — no extensions are placed.
 * The model may visually extend beyond the single block, but neighbouring cells
 * remain free so other blocks can be placed on top of / next to the carpet.
 */
public class CarpetDecorBlock extends MapDecorStaticBlock {

    public CarpetDecorBlock(Properties properties, String modelId) {
        super(properties, modelId);
    }

    @Override
    protected Set<CellOffset> localOccupiedOffsets() {
        return Set.of(CellOffset.ZERO);
    }
}
