# 根据武器特殊技能类型更新对应的冷却bossbar

# 检查特殊技能类型（shift+右键，通过标签识别）
execute if entity @s[tag=sd_using_whirlwind] run function stardew:combat/cooldown/update_whirlwind
execute if entity @s[tag=sd_using_stellar] run function stardew:combat/cooldown/update_stellar_impact
execute if entity @s[tag=sd_using_lava_eruption] run function stardew:combat/cooldown/update_lava_eruption
execute if entity @s[tag=sd_using_neptune_wrath] run function stardew:combat/cooldown/update_neptune_wrath
execute if entity @s[tag=sd_using_rapid_strike] run function stardew:combat/cooldown/update_rapid_strike
execute if entity @s[tag=sd_using_poison_blade] run function stardew:combat/cooldown/update_poison_blade
execute if entity @s[tag=sd_using_shadow_reap] run function stardew:combat/cooldown/update_shadow_reap
execute if entity @s[tag=sd_using_poison_burst] run function stardew:combat/cooldown/update_poison_burst
execute if entity @s[tag=sd_using_galaxy_awakening] run function stardew:combat/cooldown/update_galaxy_awakening
execute if entity @s[tag=sd_using_dragon_fury] run function stardew:combat/cooldown/update_dragon_fury
execute if entity @s[tag=sd_using_titan_wrath] run function stardew:combat/cooldown/update_titan_wrath
execute if entity @s[tag=sd_using_infinity_awakening] run function stardew:combat/cooldown/update_infinity_awakening
execute if entity @s[tag=sd_using_critical_surge] run function stardew:combat/cooldown/update_critical_surge
execute if entity @s[tag=sd_using_heavy_charge] run function stardew:combat/cooldown/update_heavy_charge
# 如果没有特定技能标签，使用通用更新（兼容旧武器）
execute unless entity @s[tag=sd_using_whirlwind] unless entity @s[tag=sd_using_stellar] unless entity @s[tag=sd_using_lava_eruption] unless entity @s[tag=sd_using_neptune_wrath] unless entity @s[tag=sd_using_rapid_strike] unless entity @s[tag=sd_using_poison_blade] unless entity @s[tag=sd_using_shadow_reap] unless entity @s[tag=sd_using_poison_burst] unless entity @s[tag=sd_using_galaxy_awakening] unless entity @s[tag=sd_using_dragon_fury] unless entity @s[tag=sd_using_titan_wrath] unless entity @s[tag=sd_using_infinity_awakening] unless entity @s[tag=sd_using_critical_surge] unless entity @s[tag=sd_using_heavy_charge] run function stardew:combat/cooldown/update_skill_2
