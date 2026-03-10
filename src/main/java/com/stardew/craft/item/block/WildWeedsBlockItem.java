package com.stardew.craft.item.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.nature.WildWeedsBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 野草方块物品，携带季节和变体信息
 */
public class WildWeedsBlockItem extends BlockItem {
    private final int season;
    private final int variant;
    private final String seasonName;

    @SuppressWarnings("null")
    public WildWeedsBlockItem(Block block, int season, int variant, Properties properties) {
        super(block, properties);
        this.season = season;
        this.variant = variant;
        // 根据季节设置名称
        this.seasonName = switch (season) {
            case 0 -> "spring";
            case 1 -> "summer";
            case 2 -> "fall";
            case 3 -> "winter";
            default -> "spring";
        };
    }

    @SuppressWarnings("null")
    @Override
    protected boolean updateCustomBlockEntityTag(@SuppressWarnings("null") net.minecraft.core.BlockPos pos, @SuppressWarnings("null") net.minecraft.world.level.Level level,
            @SuppressWarnings("null") net.minecraft.world.entity.player.Player player, @SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") BlockState state) {
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    /**
     * 返回唯一的描述ID，这样每个野草变体都有自己的翻译和图标
     */
    @Override
    public String getDescriptionId() {
        return "item." + StardewCraft.MODID + ".wild_weeds_" + seasonName + "_" + variant;
    }

    /**
     * 放置方块时设置正确的状态
     */
    @SuppressWarnings("null")
    @Override
    protected BlockState getPlacementState(@SuppressWarnings("null") net.minecraft.world.item.context.BlockPlaceContext context) {
        @SuppressWarnings("null")
        BlockState state = super.getPlacementState(context);
        if (state != null && state.getBlock() instanceof WildWeedsBlock) {
            state = state.setValue(WildWeedsBlock.SEASON, this.season)
                        .setValue(WildWeedsBlock.VARIANT, this.variant);
        }
        return state;
    }

    public int getSeason() {
        return season;
    }

    public int getVariant() {
        return variant;
    }
}
