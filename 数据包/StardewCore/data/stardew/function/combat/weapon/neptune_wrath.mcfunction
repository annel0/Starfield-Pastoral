# 海王怒涛 (Neptune's Wrath)
# 召唤海浪冲击前方3×5格区域，造成120%伤害+击退+减速

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 100
tag @s add sd_using_neptune_wrath
function stardew:combat/cooldown/set_neptune_wrath_cooldown_max

# 播放音效
playsound minecraft:entity.player.splash player @a ~ ~ ~ 2 0.8
playsound minecraft:block.water.ambient player @a ~ ~ ~ 2 1.2
playsound minecraft:item.trident.return player @a ~ ~ ~ 1.5 0.9

# 提示
title @s subtitle [{"text":"🌊 海王怒涛","color":"#00CED1","bold":true},{"text":" - 120%伤害+击退","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 计算伤害（120%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #120 sd_const 120
scoreboard players operation #damage sd_temp *= #120 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 生成海浪粒子效果（3层波浪，每层1.5格宽）
execute anchored eyes positioned ^ ^ ^1 run function stardew:combat/weapon/neptune_wrath_wave
execute anchored eyes positioned ^ ^ ^2.5 run function stardew:combat/weapon/neptune_wrath_wave
execute anchored eyes positioned ^ ^ ^4 run function stardew:combat/weapon/neptune_wrath_wave

# 标记范围内的敌人（前方5格内，左右各1.5格）
execute positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3] add sd_neptune_target

# 对每个敌人造成伤害
execute as @e[tag=sd_neptune_target] at @s run function stardew:combat/weapon/neptune_wrath_damage

# 清理标记
tag @e[tag=sd_neptune_target] remove sd_neptune_target
