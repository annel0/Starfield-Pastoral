# 旋风斩技能 (Whirlwind)
# 360°范围攻击，3格半径内所有敌人造成80%伤害

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 120
function stardew:combat/cooldown/set_whirlwind_cooldown_max

# 标记正在使用旋风斩技能冷却
tag @s add sd_using_whirlwind

# 播放音效
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.5 0.8
playsound minecraft:item.trident.riptide_3 player @a ~ ~ ~ 1 1.2
playsound minecraft:entity.wither.shoot player @a ~ ~ ~ 0.5 1.5

# 旋转粒子效果
execute rotated ~ 0 run function stardew:combat/weapon/whirlwind_particles

# 提示
title @s subtitle [{"text":"🌀 旋风斩","color":"gold","bold":true},{"text":" - 80%范围伤害","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 标记范围内的敌人
tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3.5] add sd_whirlwind_target

# 计算伤害（80%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #80 sd_const 80
scoreboard players operation #damage sd_temp *= #80 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 对每个敌人造成伤害
execute as @e[tag=sd_whirlwind_target] at @s run function stardew:combat/weapon/whirlwind_damage

# 清理标记
tag @e[tag=sd_whirlwind_target] remove sd_whirlwind_target
