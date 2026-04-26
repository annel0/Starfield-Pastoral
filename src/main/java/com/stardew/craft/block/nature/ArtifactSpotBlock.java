package com.stardew.craft.block.nature;

import com.stardew.craft.block.ModBlocks;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

/**
 * 远古斑点方块（Artifact Spot）— 黄土方块的变体，top 贴图带有虫点标记。
 * 被锄头锄时恢复为基础地块并掉落古物（委托给 ArtifactDropService）。
 *
 * <p>SDV: Object (O)590 "Artifact Spot" — 外观为地上的三条蠕虫。
 * 玩家用锄头挖掘可获得地点专属的古物/矿石/黏土等。
 */
@SuppressWarnings("null")
public class ArtifactSpotBlock extends Block {
    public static final BooleanProperty SAND_BACKED = BooleanProperty.create("sand_backed");

    private final Block tilledTarget;
    @Nullable
    private final Block alternateTilledTarget;

    public ArtifactSpotBlock(Properties properties) {
        this(properties, ModBlocks.YELLOW_DIRT.get(), null);
    }

    public ArtifactSpotBlock(Properties properties, Block tilledTarget) {
        this(properties, tilledTarget, null);
    }

    public ArtifactSpotBlock(Properties properties, Block tilledTarget, @Nullable Block alternateTilledTarget) {
        super(properties);
        this.tilledTarget = tilledTarget;
        this.alternateTilledTarget = alternateTilledTarget;
        registerDefaultState(this.stateDefinition.any().setValue(SAND_BACKED, false));
    }

    public BlockState stateForUnderlying(BlockState underlyingState) {
        if (alternateTilledTarget == null) {
            return defaultBlockState();
        }
        return defaultBlockState().setValue(SAND_BACKED, underlyingState.is(Blocks.SAND));
    }

    public BlockState resolveUnderlyingState(BlockState state) {
        if (alternateTilledTarget != null && state.hasProperty(SAND_BACKED) && state.getValue(SAND_BACKED)) {
            return alternateTilledTarget.defaultBlockState();
        }
        return tilledTarget.defaultBlockState();
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context,
                                           ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.HOE_TILL) {
            return resolveUnderlyingState(state);
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SAND_BACKED);
    }
}
