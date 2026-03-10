# 剧毒之刃技能 (Poison Blade)
# 涂抹剧毒，造成80%武器伤害 + 持续毒伤

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 160
function stardew:combat/cooldown/set_poison_blade_cooldown_max

# 标记正在使用剧毒之刃技能冷却
tag @s add sd_using_poison_blade

# 播放音效
playsound minecraft:entity.spider.ambient player @a ~ ~ ~ 1.5 0.7
playsound minecraft:entity.zombie_villager.cure player @a ~ ~ ~ 0.8 1.8
playsound minecraft:block.brewing_stand.brew player @a ~ ~ ~ 1 1.5

# 视觉效果
particle minecraft:effect ~ ~1 ~ 0.3 0.3 0.3 0.3 30 force
particle minecraft:sneeze ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:item_slime ~ ~1 ~ 0.3 0.3 0.3 0.1 15 force

# 提示
title @s subtitle [{"text":"☠ 剧毒之刃","color":"#228B22","bold":true},{"text":" - 持续毒伤","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 标记前方5格内的所有敌人
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3] add sd_poison_target

# 计算初始伤害（80%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #80 sd_const 80
scoreboard players operation #damage sd_temp *= #80 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 获取毒伤害参数
execute store result score #poison_damage sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_poison_damage
execute unless score #poison_damage sd_temp matches 1.. run scoreboard players set #poison_damage sd_temp 8
execute store result score #poison_duration sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_poison_duration
execute unless score #poison_duration sd_temp matches 1.. run scoreboard players set #poison_duration sd_temp 100

# 对目标造成伤害并施加中毒
execute as @e[tag=sd_poison_target] at @s run function stardew:combat/weapon/poison_blade_damage

# 清理标记
tag @e[tag=sd_poison_target] remove sd_poison_target
