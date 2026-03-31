package com.stardew.craft.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public final class ModRenderLayers {
    private ModRenderLayers() {
    }

    @SuppressWarnings({"null", "deprecation"})
    public static void registerCutout(Iterable<Block> blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
        }
    }
}
