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
 * SDV 戒指物品基类
 * 不使用 MC 原版装备系统，纯自定义逻辑，通过 V 键 UI 穿戴。
 */
@SuppressWarnings("null")
public class StardewRingItem extends Item implements IStardewItem {

    private final RingType ringType;
    private final int sellPrice;

    public StardewRingItem(RingType ringType, int sellPrice, Properties properties) {
        super(properties.stacksTo(1));
        this.ringType = ringType;
        this.sellPrice = sellPrice;
    }

    public RingType getRingType() {
        return ringType;
    }

    /**
     * 获取该戒指提供的装备属性（Buff 型效果）
     */
    public EquipmentStats getEquipmentStats() {
        return ringType.buildStats();
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.ring";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return sellPrice;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        EquipmentStats stats = getEquipmentStats();
        if (stats.getDefense() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.defense", stats.getDefense())
                    .withStyle(ChatFormatting.BLUE));
        }
        if (stats.getImmunity() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.immunity", stats.getImmunity())
                    .withStyle(ChatFormatting.YELLOW));
        }
        // 攻击倍率型戒指（Ruby Ring / Iridium Band）
        if (ringType.isAttackMultiplier()) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.attack",
                    String.format("+%d%%", Math.round(ringType.getAttackMultiplier() * 100)))
                    .withStyle(ChatFormatting.RED));
        }
        if (stats.getCritChance() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.crit_chance",
                    String.format("+%d%%", Math.round(stats.getCritChance() * 100)))
                    .withStyle(ChatFormatting.RED));
        }
        if (stats.getCritPower() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.crit_power",
                    String.format("+%d%%", Math.round(stats.getCritPower() * 100)))
                    .withStyle(ChatFormatting.RED));
        }
        if (stats.getKnockbackBonus() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.knockback",
                    String.format("+%d%%", Math.round(stats.getKnockbackBonus() * 100)))
                    .withStyle(ChatFormatting.GOLD));
        }
        if (stats.getMagneticRadius() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.magnetic_radius", stats.getMagneticRadius())
                    .withStyle(ChatFormatting.AQUA));
        }
        if (stats.getLuck() > 0) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.luck", String.format("+%.0f", stats.getLuck()))
                    .withStyle(ChatFormatting.GREEN));
        }
        // 武器速度型戒指
        if (ringType.isWeaponSpeedMultiplier()) {
            tooltipComponents.add(Component.translatable("stardewcraft.tooltip.weapon_speed",
                    String.format("+%d%%", Math.round(ringType.getWeaponSpeedMultiplier() * 100)))
                    .withStyle(ChatFormatting.YELLOW));
        }

        // 特殊效果描述
        String descKey = "stardewcraft.ring." + ringType.name().toLowerCase() + ".desc";
        if (ringType.hasSpecialEffect()) {
            tooltipComponents.add(Component.translatable(descKey).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }
}
