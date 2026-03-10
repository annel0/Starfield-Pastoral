# 熔岩爆发技能 (Lava Eruption)
# 地面喷发熔岩火柱，5格范围AOE，200%伤害+5秒燃烧

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 240
function stardew:combat/cooldown/set_lava_eruption_cooldown_max

# 标记正在使用熔岩爆发技能冷却
tag @s add sd_using_lava_eruption

# 播放强力音效
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 2 0.5
playsound minecraft:entity.blaze.shoot player @a ~ ~ ~ 2 0.6
playsound minecraft:block.fire.extinguish player @a ~ ~ ~ 1.5 0.8
playsound minecraft:item.trident.thunder player @a ~ ~ ~ 1 0.7

# 地面震动效果
particle minecraft:block{block_state:"minecraft:magma_block"} ~ ~0.1 ~ 2 0.1 2 0 100 force
particle minecraft:lava ~ ~0.1 ~ 2.5 0 2.5 0 30 force
particle minecraft:explosion ~ ~0.5 ~ 1.5 0 1.5 0 10 force

# 视觉提示 - 地面龟裂
title @s subtitle [{"text":"🌋 熔岩爆发","color":"#FF4500","bold":true},{"text":" - 火山之怒","color":"dark_red"}]
title @s times 0 40 10
title @s title {"text":""}

# 标记范围内的所有敌人
execute positioned ~ ~0.5 ~ run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,distance=..5] add sd_lava_target

# 获取武器基础伤害并计算200%伤害
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
# 150%伤害（平衡调整，原200%太高）
scoreboard players set #150 sd_const 150
scoreboard players set #100 sd_const 100
scoreboard players operation #damage sd_temp *= #150 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 对每个敌人造成伤害+燃烧，并生成独立的火柱效果
execute as @e[tag=sd_lava_target] at @s run function stardew:combat/weapon/lava_eruption_hit

# 清理标记
tag @e[tag=sd_lava_target] remove sd_lava_target

# 持续的火焰粒子效果（使用schedule）
schedule function stardew:combat/weapon/lava_eruption_particles 1t
schedule function stardew:combat/weapon/lava_eruption_particles 5t
schedule function stardew:combat/weapon/lava_eruption_particles 10t
schedule function stardew:combat/weapon/lava_eruption_particles 15t
