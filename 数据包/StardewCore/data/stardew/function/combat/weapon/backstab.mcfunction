# 背刺技能 (Backstab)
# 对单个目标造成致命一击，必定暴击

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 先标记前方的敌人，检查是否有目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3,limit=1,sort=nearest] add sd_backstab_target

# 如果没有目标，提示并返回（不进入冷却）
execute unless entity @e[tag=sd_backstab_target] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_backstab_target] run return 0

# 有目标时才进入冷却
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 100
function stardew:combat/cooldown/set_backstab_cooldown_max

# 标记正在使用背刺技能冷却
tag @s add sd_using_backstab

# 播放音效
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 0.8
playsound minecraft:entity.ender_dragon.flap player @a ~ ~ ~ 0.8 1.8
playsound minecraft:item.trident.throw player @a ~ ~ ~ 1 1.5

# 视觉效果
particle minecraft:crit ~ ~1 ~ 0.3 0.3 0.3 0.3 30 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.2 20 force
particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 1 force

# 提示
title @s subtitle [{"text":"🗡 背刺","color":"#8B4513","bold":true},{"text":" - 致命一击","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 计算基础伤害（100%，将在damage函数中应用暴击倍率）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 对目标造成伤害（必定暴击）
execute as @e[tag=sd_backstab_target] at @s run function stardew:combat/weapon/backstab_damage

# 清理标记
tag @e[tag=sd_backstab_target] remove sd_backstab_target
