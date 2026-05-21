package com.stardew.craft.item.furniture;

import com.stardew.craft.block.utility.OakTableBlock;
import com.stardew.craft.item.SimpleStardewItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FloralTableclothItem extends SimpleStardewItem {
    public FloralTableclothItem(Properties properties) {
        super("stardewcraft.type.furniture", -1, properties);
    }

    @Override
    @SuppressWarnings("null")
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level.getBlockState(context.getClickedPos()).getBlock() instanceof OakTableBlock)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        boolean applied = OakTableBlock.applyClothToConnectedTables(level, context.getClickedPos(), OakTableBlock.CLOTH_STYLE_FLOWER);
        if (!applied) {
            return InteractionResult.CONSUME;
        }

        level.playSound(null, context.getClickedPos(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.9F, 1.0F);
        if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
            ItemStack stack = context.getItemInHand();
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}