package com.stardew.craft.entity.npc;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;

/**
 * Custom GroundPathNavigation that uses {@link NpcNodeEvaluator} instead of
 * the vanilla WalkNodeEvaluator. This makes NPCs prefer roads and paved
 * surfaces when planning paths.
 */
public class NpcPathNavigation extends GroundPathNavigation {

    private static final int MIN_NPC_PATH_NODE_BUDGET = 8192;

    public NpcPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new NpcNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        this.nodeEvaluator.setCanOpenDoors(true);
        this.nodeEvaluator.setCanFloat(false);
        return new PathFinder(this.nodeEvaluator, Math.max(maxVisitedNodes, MIN_NPC_PATH_NODE_BUDGET));
    }
}
