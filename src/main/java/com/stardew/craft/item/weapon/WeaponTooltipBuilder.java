package com.stardew.craft.item.weapon;

import com.stardew.craft.client.ModKeyMappings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 武器Tooltip构建器
 * 生成美观的武器描述信息
 */
public class WeaponTooltipBuilder {
    
    @SuppressWarnings("unused")
    private final ItemStack stack;
    private final WeaponData data;
    private final List<Component> lines = new ArrayList<>();
    
    public WeaponTooltipBuilder(ItemStack stack, WeaponData data) {
        this.stack = stack;
        this.data = data;
    }
    
    /**
     * 构建完整的tooltip
     */
    public List<Component> build() {
        lines.clear();
        
        addEmptyLine();
        addWeaponLevel();
        addAttributes();
        addEmptyLine();
        addSeparator();
        addEmptyLine();
        addSkills();
        addEmptyLine();
        addSeparator();
        
        return lines;
    }
    
    /**
     * 添加武器类型和等级行
     * 格式: ⚔ 剑 │ Lv.1
     */
    @SuppressWarnings("null")
    private void addWeaponLevel() {
        MutableComponent line = Component.empty();

        line.append(Component.translatable("stardewcraft.weapon.tooltip.weapon_level", data.getLevel())
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        
        lines.add(line);
    }
    
    /**
     * 添加伤害行
     * 格式: ⚔ 2-5 伤害
     */
    @SuppressWarnings("null")
    private MutableComponent buildDamage() {
        MutableComponent line = Component.empty();
        line.append(WeaponIcons.icon(WeaponIcons.ICON_DAMAGE));
        line.append(Component.translatable("stardewcraft.weapon.tooltip.damage_range", data.getDamageMin(), data.getDamageMax())
                .withStyle(ChatFormatting.RED));
        line.append(Component.translatable("stardewcraft.weapon.tooltip.damage").withStyle(ChatFormatting.GRAY));
        return line;
    }
    
    /**
     * 添加属性行（速度、防御、重量、暴击）
     */
    @SuppressWarnings("null")
    private void addAttributes() {
        MutableComponent damageLine = buildDamage();
        MutableComponent speedLine = buildAttribute(WeaponIcons.ICON_SPEED, "stardewcraft.weapon.tooltip.speed", data.getSpeed(), true);
        lines.add(buildPairLine(damageLine, speedLine));

        double critChance = data.getCritChance();
        MutableComponent critLine = Component.empty();
        critLine.append(WeaponIcons.icon(WeaponIcons.ICON_CRIT_CHANCE));
        critLine.append(Component.translatable("stardewcraft.weapon.tooltip.attr_value", formatPercent(critChance))
                .withStyle(ChatFormatting.YELLOW));
        critLine.append(Component.translatable("stardewcraft.weapon.tooltip.crit_chance").withStyle(ChatFormatting.GRAY));

        int critPowerBonus = (int) Math.round((data.getCritPower() - 1.0) * 100.0);
        MutableComponent critPowerLine = Component.empty();
        critPowerLine.append(WeaponIcons.icon(WeaponIcons.ICON_CRIT_POWER));
        critPowerLine.append(Component.translatable("stardewcraft.weapon.tooltip.crit_damage_value", critPowerBonus)
                .withStyle(ChatFormatting.GOLD));
        critPowerLine.append(Component.translatable("stardewcraft.weapon.tooltip.crit_damage").withStyle(ChatFormatting.GRAY));

        lines.add(buildPairLine(critLine, critPowerLine));

        java.util.List<MutableComponent> extras = new java.util.ArrayList<>();
        if (data.getDefense() != 0) {
            extras.add(buildAttribute(WeaponIcons.ICON_DEFENSE, "stardewcraft.weapon.tooltip.defense", data.getDefense(), true));
        }
        if (data.getWeight() != 0) {
            extras.add(buildAttribute(WeaponIcons.ICON_WEIGHT, "stardewcraft.weapon.tooltip.weight", data.getWeight(), true));
        }
        for (int i = 0; i < extras.size(); i += 2) {
            MutableComponent left = extras.get(i);
            MutableComponent right = (i + 1 < extras.size()) ? extras.get(i + 1) : null;
            lines.add(buildPairLine(left, right));
        }
    }
    
    @SuppressWarnings("null")
    private MutableComponent buildAttribute(String icon, String nameKey, int value, boolean showSign) {
        MutableComponent line = Component.empty();
        ChatFormatting color = value > 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
        line.append(WeaponIcons.icon(icon));

        String valueStr = showSign ? (value > 0 ? "+" + value : String.valueOf(value)) : String.valueOf(value);
        line.append(Component.translatable("stardewcraft.weapon.tooltip.attr_value", valueStr).withStyle(color));
        line.append(Component.translatable(nameKey).withStyle(ChatFormatting.GRAY));
        return line;
    }

    @SuppressWarnings("null")
    private MutableComponent buildPairLine(MutableComponent left, MutableComponent right) {
        MutableComponent line = Component.empty();
        line.append(Component.literal(" "));
        line.append(left);
        if (right != null) {
            line.append(Component.literal("   "));
            line.append(right);
        }
        return line;
    }
    
    /**
     * 添加技能信息
     */
    private void addSkills() {
        WeaponSkillData skill1 = data.getSkill1();
        WeaponSkillData skill2 = data.getSkill2();
        if (skill1 == null && skill2 == null) {
            return;
        }

        String minorKeyLabel = getKeyLabel(true);
        String majorKeyLabel = getKeyLabel(false);

        if (skill1 != null && skill2 != null) {
            lines.add(buildSkillTitle(skill1, minorKeyLabel));
            addSkillDetails(skill1);
            addEmptyLine();
            lines.add(buildSkillTitle(skill2, majorKeyLabel));
            addSkillDetails(skill2);
            return;
        }

        if (skill1 != null) {
            lines.add(buildSkillTitle(skill1, minorKeyLabel));
            addSkillDetails(skill1);
        }

        if (skill2 != null) {
            lines.add(buildSkillTitle(skill2, majorKeyLabel));
            addSkillDetails(skill2);
        }
    }
    
    /**
     * 添加单个技能区块
     */
    @SuppressWarnings("null")
    private MutableComponent buildSkillTitle(WeaponSkillData skill, String keyLabel) {
        MutableComponent titleLine = Component.empty();

        if (skill.getIconChar() != null) {
            titleLine.append(WeaponIcons.icon(skill.getIconChar()));
            titleLine.append(Component.literal(" "));
        } else {
            titleLine.append(WeaponIcons.icon(WeaponIcons.ICON_SKILL));
            titleLine.append(Component.literal(" "));
        }

        titleLine.append(Component.translatable(skill.getNameKey()).withStyle(ChatFormatting.YELLOW));
        titleLine.append(Component.translatable("stardewcraft.weapon.tooltip.trigger_format", Component.literal(keyLabel))
                .withStyle(ChatFormatting.DARK_GRAY));
        return titleLine;
    }

    @SuppressWarnings("null")
    private void addSkillDetails(WeaponSkillData skill) {
        for (String descKey : skill.getDescriptionKeys()) {
            lines.add(Component.translatable("stardewcraft.weapon.tooltip.skill_desc", Component.translatable(descKey))
                    .withStyle(ChatFormatting.GRAY));
        }
        
        addEmptyLine();
        
        // 技能详细数据
        // 伤害
        if (skill.getDamagePercent() > 0) {
            MutableComponent damageLine = Component.empty();
                damageLine.append(Component.translatable("stardewcraft.weapon.tooltip.effect_prefix").withStyle(ChatFormatting.DARK_GRAY));
                damageLine.append(Component.translatable("stardewcraft.weapon.tooltip.skill_damage", skill.getDamagePercent())
                    .withStyle(ChatFormatting.AQUA));
            lines.add(damageLine);
        }
        
        // 特殊效果
        for (String effectKey : skill.getEffectKeys()) {
            MutableComponent effectLine = Component.empty();
            effectLine.append(Component.translatable("stardewcraft.weapon.tooltip.effect_prefix").withStyle(ChatFormatting.DARK_GRAY));
            effectLine.append(Component.translatable(effectKey).withStyle(ChatFormatting.GRAY));
            lines.add(effectLine);
        }
        
        // 冷却时间（最后一项用 └）
        MutableComponent cooldownLine = Component.empty();
        cooldownLine.append(Component.translatable("stardewcraft.weapon.tooltip.cooldown_prefix").withStyle(ChatFormatting.DARK_GRAY));
        cooldownLine.append(WeaponIcons.icon(WeaponIcons.ICON_COOLDOWN));
        cooldownLine.append(Component.translatable("stardewcraft.weapon.tooltip.cooldown_seconds", skill.getCooldown())
                .withStyle(ChatFormatting.BLUE));
        cooldownLine.append(Component.translatable("stardewcraft.weapon.tooltip.cooldown").withStyle(ChatFormatting.GRAY));
        lines.add(cooldownLine);
    }

    @SuppressWarnings({ "null", "unused" })
    private MutableComponent mergeSkillTitles(MutableComponent left, MutableComponent right) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.font == null) {
            return left.append(Component.literal("  ")).append(right);
        }

        @SuppressWarnings("null")
        int leftWidth = mc.font.width(left.getString());
        int spaceWidth = Math.max(1, mc.font.width(" "));
        int targetColumn = Math.max(120, leftWidth + (spaceWidth * 2));
        int padPx = Math.max(spaceWidth * 2, targetColumn - leftWidth);
        int padSpaces = (int) Math.ceil(padPx / (double) spaceWidth);

        return left.append(Component.literal(" ".repeat(padSpaces))).append(right);
    }
    
    
    /**
     * 添加分隔线
     */
    private void addSeparator() {
        lines.add(WeaponIcons.separator());
    }
    
    /**
     * 添加空行
     */
    private void addEmptyLine() {
        lines.add(Component.empty());
    }
    
    private String getKeyLabel(boolean minor) {
        try {
            if (minor) {
                return ModKeyMappings.SKILL_MINOR.getTranslatedKeyMessage().getString();
            }
            return ModKeyMappings.SKILL_MAJOR.getTranslatedKeyMessage().getString();
        } catch (Throwable ignored) {
            return minor ? "RMB" : "Z";
        }
    }
    
    /**
     * 格式化百分比
     */
    private String formatPercent(double value) {
        return (int)(value * 100) + "%";
    }
    
}
