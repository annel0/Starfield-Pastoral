package com.stardew.craft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.List;

public class StoneConvertibleBlockItem extends BlockItem {
    public StoneConvertibleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack,
                                @Nonnull TooltipContext context,
                                @Nonnull List<Component> tooltipComponents,
                                @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("stardewcraft.tooltip.convertible_to_stone_in_stardew_crafting")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
    }
}
