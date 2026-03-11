package com.stardew.craft.item.tool;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.deco.DecorationService;
import com.stardew.craft.deco.DecorationType;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PaintbrushItem extends Item implements IStardewItem {
    public PaintbrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.tool";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return -1;
    }

    @Override
    @SuppressWarnings("null")
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clicked = level.getBlockState(pos);

        DecorationType type = null;
        if (clicked.getBlock() == ModBlocks.WALLPAPER_BLOCK.get()) {
            type = DecorationType.WALLPAPER;
        } else if (clicked.getBlock() == ModBlocks.FLOORING_BLOCK.get()) {
            type = DecorationType.FLOORING;
        }

        if (type == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
            DecorationService.openSelection(serverPlayer, pos, type);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
