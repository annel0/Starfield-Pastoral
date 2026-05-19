package com.stardew.craft.block.decor;

import com.stardew.craft.manager.SecretWoodsAccessManager;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SecretWoodsEntranceLogBlock extends ResourceClumpBlock {
    public SecretWoodsEntranceLogBlock(Properties properties,
                                      String modelId,
                                      RequiredTool requiredTool,
                                      int requiredTier,
                                      float health,
                                      Supplier<Item> dropItem,
                                      int dropCount,
                                      @Nullable Supplier<Item> bonusDropItem,
                                      int bonusDropCount,
                                      double bonusDropChance,
                                      @Nullable SkillType experienceSkill,
                                      int experienceAmount,
                                      double minX,
                                      double minZ,
                                      double maxX,
                                      double maxY,
                                      double maxZ) {
        super(properties, modelId, requiredTool, requiredTier, health, dropItem, dropCount,
                bonusDropItem, bonusDropCount, bonusDropChance, experienceSkill, experienceAmount,
                minX, minZ, maxX, maxY, maxZ);
    }

    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state,
                                        @Nonnull BlockGetter level,
                                        @Nonnull BlockPos pos,
                                        @Nonnull CollisionContext context) {
        if (SecretWoodsAccessManager.shouldIgnoreEntranceLogCollision(level, pos, state, context)) {
            return Shapes.empty();
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public void breakClump(ServerLevel level, BlockPos pos, BlockState state, @Nullable ServerPlayer player) {
        if (SecretWoodsAccessManager.isEntranceLog(level, pos, state)) {
            SecretWoodsAccessManager.breakEntranceLog(level, pos, state, player);
            return;
        }
        super.breakClump(level, pos, state, player);
    }
}