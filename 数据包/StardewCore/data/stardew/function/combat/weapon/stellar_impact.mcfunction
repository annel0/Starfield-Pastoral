# 星辰冲击技能 (Stellar Impact)
# 释放能量波攻击前方直线敌人

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 140
function stardew:combat/cooldown/set_stellar_impact_cooldown_max

# 标记正在使用星辰冲击技能冷却
tag @s add sd_using_stellar

# 播放音效
playsound minecraft:entity.wither.shoot player @a ~ ~ ~ 2 0.8
playsound minecraft:entity.lightning_bolt.thunder player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.guardian.attack player @a ~ ~ ~ 1.5 0.8

# 视觉效果 - 起始爆发
particle minecraft:explosion ~ ~1 ~ 0 0 0 0 2 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 1 force
particle minecraft:end_rod ~ ~1 ~ 0.3 0.3 0.3 0.3 30 force

# 提示
title @s subtitle [{"text":"⭐ 星辰冲击","color":"#9933FF","bold":true},{"text":" - 能量波","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 读取技能伤害倍率和范围（从武器custom_data）
execute store result score #stellar_mult sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_stellar_damage_mult
execute unless score #stellar_mult sd_temp matches 1.. run scoreboard players set #stellar_mult sd_temp 150

execute store result score #stellar_range sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_stellar_range
execute unless score #stellar_range sd_temp matches 1.. run scoreboard players set #stellar_range sd_temp 5

# 计算伤害
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players operation #damage sd_temp *= #stellar_mult sd_temp
scoreboard players operation #damage sd_temp /= #100 sd_const

# 沿视线方向释放能量波
function stardew:combat/weapon/stellar_impact_wave

# 对所有被标记的敌人造成伤害（只执行一次）
execute as @e[tag=sd_stellar_damaged] at @s run function stardew:combat/weapon/stellar_impact_damage

# 清理标记
tag @e[tag=sd_stellar_hit] remove sd_stellar_hit
tag @e[tag=sd_stellar_damaged] remove sd_stellar_damaged

