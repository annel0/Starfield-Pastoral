package com.stardew.craft.block.nature;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

/**
 * 远古斑点方块（Artifact Spot）— 黄土方块的变体，top 贴图带有虫点标记。
 * 被锄头锄时变成耕地并掉落古物（委托给 ArtifactDropService）。
 *
 * <p>SDV: Object (O)590 "Artifact Spot" — 外观为地上的三条蠕虫。
 * 玩家用锄头挖掘可获得地点专属的古物/矿石/黏土等。
 */
@SuppressWarnings("null")
public class ArtifactSpotBlock extends Block {

    public ArtifactSpotBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context,
                                           ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.HOE_TILL) {
            // 锄头可以翻耕此方块
            return Blocks.FARMLAND.defaultBlockState();
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
