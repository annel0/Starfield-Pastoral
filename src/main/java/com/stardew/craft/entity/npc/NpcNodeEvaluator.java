package com.stardew.craft.entity.npc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.Set;

/**
 * Custom WalkNodeEvaluator that assigns higher traversal cost to "off-road"
 * blocks, making NPCs strongly prefer roads, paths, planks and other
 * constructed surfaces — just like Stardew Valley originals.
 *
 * <p>Road/paved blocks: costMalus stays at vanilla default (0).
 * Off-road natural terrain: costMalus += {@link #OFF_ROAD_PENALTY}.
 * This causes the A* inside vanilla PathFinder to strongly prefer road nodes
 * when a road path exists, while still allowing off-road navigation when
 * no road is available.</p>
 */
public class NpcNodeEvaluator extends WalkNodeEvaluator {

    /** Extra cost SET on WALKABLE nodes whose surface is NOT a road block. */
    private static final float OFF_ROAD_PENALTY = 4.0F;

    /**
     * Set of vanilla blocks considered "road" or "paved" surfaces.
     * NPCs will strongly prefer walking on these.
     */
    private static final Set<Block> ROAD_BLOCKS = Set.of(
        // Vanilla paths
        Blocks.DIRT_PATH,
        // Coarse dirt
        Blocks.COARSE_DIRT,
        // Cobblestone roads
        Blocks.COBBLESTONE, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE_STAIRS,
        Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_STAIRS,
        // Stone roads
        Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_STAIRS,
        Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB,
        Blocks.STONE_BRICKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICK_STAIRS,
        Blocks.MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_STAIRS,
        // Wooden bridges/walkways
        Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS,
        Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS,
        Blocks.MANGROVE_PLANKS, Blocks.CHERRY_PLANKS, Blocks.BAMBOO_PLANKS,
        Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.BIRCH_SLAB,
        Blocks.JUNGLE_SLAB, Blocks.JUNGLE_STAIRS,
        Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS,
        Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.BIRCH_STAIRS,
        // Bricks
        Blocks.BRICKS, Blocks.BRICK_SLAB, Blocks.BRICK_STAIRS,
        // Gravel paths
        Blocks.GRAVEL,
        // Sandstone
        Blocks.SANDSTONE, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE_STAIRS,
        Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE_STAIRS,
        // Deepslate
        Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICK_STAIRS,
        Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILE_STAIRS,
        // Polished variants
        Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_STAIRS,
        Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE_STAIRS,
        Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE_STAIRS,
        // Misc
        Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_STAIRS
    );

    @Override
    public int getNeighbors(@javax.annotation.Nonnull Node[] buffer, @javax.annotation.Nonnull Node node) {
        int count = super.getNeighbors(buffer, node);
        if (this.currentContext == null) return count;

        BlockGetter level = this.currentContext.level();
        for (int i = 0; i < count; i++) {
            Node neighbor = buffer[i];
            if (neighbor.type != PathType.WALKABLE
                && neighbor.type != PathType.DOOR_OPEN
                && neighbor.type != PathType.DOOR_WOOD_CLOSED) {
                continue;
            }

            // The surface block is the one directly below the node's feet
            BlockPos surfacePos = new BlockPos(neighbor.x, neighbor.y - 1, neighbor.z);
            BlockState surface = level.getBlockState(surfacePos);
            Block surfaceBlock = surface.getBlock();

            // SET costMalus (idempotent), NOT +=.
            // Nodes are cached by position in PathFinder — the same Node object
            // is returned as a neighbor of multiple parent nodes. Using += would
            // accumulate the penalty each time, inflating cost to infinity and
            // making the pathfinder give up.
            if (!isRoadBlock(surfaceBlock)) {
                neighbor.costMalus = OFF_ROAD_PENALTY;
            }
            // Road blocks keep vanilla default costMalus (0.0)
        }
        return count;
    }

    /**
     * Check if a block is considered a "road" surface.
     * Road blocks get no extra malus; off-road blocks get penalized.
     */
    private static boolean isRoadBlock(Block block) {
        if (ROAD_BLOCKS.contains(block)) return true;
        // Mod blocks (resolved at runtime to avoid class-load ordering issues)
        if (block == com.stardew.craft.block.ModBlocks.YELLOW_DIRT.get()) return true;
        return false;
    }
}
