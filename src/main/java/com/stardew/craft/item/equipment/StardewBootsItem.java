package com.stardew.craft.item.equipment;

import com.stardew.craft.combat.equipment.EquipmentStats;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * SDV 靴子物品基类
 * 提供防御和免疫两个属性，通过 V 键 UI 穿戴。
 */
@SuppressWarnings("null")
public class StardewBootsItem extends Item implements IStardewItem {

    private final BootsType bootsType;
    private final int sellPrice;

    public StardewBootsItem(BootsType bootsType, Properties properties) {
        super(properties.stacksTo(1));
        this.bootsType = bootsType;
        this.sellPrice = bootsType.getDefense() * 100 + bootsType.getImmunity() * 100;
    }

    public BootsType getBootsType() {
        return bootsType;
    }

    public EquipmentStats getEquipmentStats() {
        return EquipmentStats.builder()
                .defense(bootsType.getDefense())
                .immunity(bootsType.getImmunity())
                .build();
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.boots";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return sellPrice;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (bootsType.getDefense() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.defense", bootsType.getDefense())
                    .withStyle(ChatFormatting.BLUE));
        }
        if (bootsType.getImmunity() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.immunity", bootsType.getImmunity())
                    .withStyle(ChatFormatting.YELLOW));
        }
    }
}
