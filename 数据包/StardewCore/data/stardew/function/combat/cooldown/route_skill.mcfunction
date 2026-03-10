# 根据武器技能类型更新对应的冷却bossbar

# 检查主技能类型（通过标签识别）
execute if entity @s[tag=sd_using_dash] run function stardew:combat/cooldown/update_dash_strike
execute if entity @s[tag=sd_using_forest_blessing] run function stardew:combat/cooldown/update_forest_blessing
execute if entity @s[tag=sd_using_astral_aegis] run function stardew:combat/cooldown/update_astral_aegis
# 火焰斩已改为独立冷却系统，不再使用sd_skill_cooldown
execute if entity @s[tag=sd_using_precision] run function stardew:combat/cooldown/update_precision_strike
execute if entity @s[tag=sd_using_backstab] run function stardew:combat/cooldown/update_backstab
execute if entity @s[tag=sd_using_shadow_step] run function stardew:combat/cooldown/update_shadow_step
execute if entity @s[tag=sd_using_poison_strike] run function stardew:combat/cooldown/update_poison_strike
execute if entity @s[tag=sd_using_star_flurry] run function stardew:combat/cooldown/update_star_flurry
execute if entity @s[tag=sd_using_blade_dance] run function stardew:combat/cooldown/update_blade_dance
execute if entity @s[tag=sd_using_ground_slam] run function stardew:combat/cooldown/update_ground_slam
execute if entity @s[tag=sd_using_meteor_strike] run function stardew:combat/cooldown/update_meteor_strike
execute if entity @s[tag=sd_using_bone_break] run function stardew:combat/cooldown/update_bone_break
execute if entity @s[tag=sd_using_rhythm_strike] run function stardew:combat/cooldown/update_rhythm_strike
# 龙牙连击已改为独立冷却系统，不再使用sd_skill_cooldown
execute if entity @s[tag=sd_using_heavy_charge] run function stardew:combat/cooldown/update_heavy_charge
# 如果没有特定技能标签，使用通用更新（兼容旧武器/格挡技能）
execute unless entity @s[tag=sd_using_dash] unless entity @s[tag=sd_using_forest_blessing] unless entity @s[tag=sd_using_astral_aegis] unless entity @s[tag=sd_using_precision] unless entity @s[tag=sd_using_backstab] unless entity @s[tag=sd_using_shadow_step] unless entity @s[tag=sd_using_poison_strike] unless entity @s[tag=sd_using_star_flurry] unless entity @s[tag=sd_using_blade_dance] unless entity @s[tag=sd_using_ground_slam] unless entity @s[tag=sd_using_meteor_strike] unless entity @s[tag=sd_using_bone_break] unless entity @s[tag=sd_using_rhythm_strike] unless entity @s[tag=sd_using_heavy_charge] run function stardew:combat/cooldown/update_skill
