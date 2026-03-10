# 突刺技能 (Dash Strike)
# 向前突进2-3格，造成150%伤害

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 80
function stardew:combat/cooldown/set_dash_strike_cooldown_max

# 标记正在使用突刺技能冷却
tag @s add sd_using_dash

# 播放音效
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.5 1.2
playsound minecraft:entity.ender_dragon.flap player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.wither.shoot player @a ~ ~ ~ 0.8 2

# 视觉效果 - 起始
particle minecraft:sweep_attack ~ ~1 ~ 0.3 0.3 0.3 0 8 force
particle minecraft:explosion ~ ~1 ~ 0 0 0 0 1 force

# 提示
title @s subtitle [{"text":"⚔ 突刺","color":"aqua","bold":true},{"text":" - 150%伤害","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 向前突进（一次性传送2.5格）
# 使用 rotated ~ 0 锁定水平方向
execute rotated ~ 0 run tp @s ^ ^ ^2.5

# 突进粒子轨迹
particle minecraft:cloud ~ ~1 ~ 0.3 0.5 0.3 0.05 15 force
particle minecraft:crit ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 3 force

# 对路径上的敌人造成伤害
tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..5] add sd_dash_target

# 计算伤害（150%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #150 sd_const 150
scoreboard players operation #damage sd_temp *= #150 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 对每个敌人造成伤害
execute as @e[tag=sd_dash_target] at @s run function stardew:combat/weapon/dash_strike_damage

# 清理标记
tag @e[tag=sd_dash_target] remove sd_dash_target
