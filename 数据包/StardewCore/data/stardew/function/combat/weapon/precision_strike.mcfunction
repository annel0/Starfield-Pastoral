# 精准打击技能 (Precision Strike)
# 提升暴击率25%，持续5秒

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 获取技能冷却时间
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 160

# 标记使用精准打击（用于bossbar）
tag @s add sd_using_precision

# 音效
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.experience_orb.pickup player @a ~ ~ ~ 1 1.8
playsound minecraft:block.amethyst_block.chime player @a ~ ~ ~ 0.8 2

# 粒子效果
particle minecraft:crit ~ ~1 ~ 0.3 0.5 0.3 0.5 30 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:end_rod ~ ~1 ~ 0.2 0.5 0.2 0.05 15 force

# 提示
title @s subtitle [{"text":"⚔ 精准打击","color":"gold","bold":true},{"text":" - 暴击率+25%","color":"yellow"}]
title @s times 0 30 10
title @s title {"text":""}

# 获取持续时间（默认5秒=100ticks）
execute store result score @s sd_precision_duration run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_precision_duration
execute unless score @s sd_precision_duration matches 1.. run scoreboard players set @s sd_precision_duration 100

# 标记精准状态
tag @s add sd_precision_active

# 设置冷却条最大值
function stardew:combat/cooldown/set_precision_strike_cooldown_max
