package com.stardew.craft.item.misc;

import com.stardew.craft.item.StardewBlockItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class LuckyPurpleShortsItem extends StardewBlockItem {
    public LuckyPurpleShortsItem(Block block, Item.Properties properties) {
        super(block, "stardewcraft.type.quest", 0, properties);
    }

    @SuppressWarnings("null")
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }
}
