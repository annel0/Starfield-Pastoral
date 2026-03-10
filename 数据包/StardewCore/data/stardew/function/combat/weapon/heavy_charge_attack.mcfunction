# 蓄力重击 - 攻击执行（支持多等级）

# 1. 获取技能等级（先尝试 weapon_skill_2_level，如果没有则用 weapon_skill_level）
execute store result score #skill_level sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_level
execute if score #skill_level sd_temp matches ..0 store result score #skill_level sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_level
execute unless score #skill_level sd_temp matches 1.. run scoreboard players set #skill_level sd_temp 1

# 2. 标记范围内所有目标（3格范围）
execute positioned ~ ~1.5 ~ positioned ^ ^ ^3 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3] add sd_heavy_charge_target

# 3. 计算基础伤害（根据等级：250%/300%/350%/400%/450%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 根据技能等级应用伤害倍率
execute if score #skill_level sd_temp matches 1 run scoreboard players set #damage_multiplier sd_temp 250
execute if score #skill_level sd_temp matches 2 run scoreboard players set #damage_multiplier sd_temp 300
execute if score #skill_level sd_temp matches 3 run scoreboard players set #damage_multiplier sd_temp 350
execute if score #skill_level sd_temp matches 4 run scoreboard players set #damage_multiplier sd_temp 400
execute if score #skill_level sd_temp matches 5.. run scoreboard players set #damage_multiplier sd_temp 450
scoreboard players operation #damage sd_temp *= #damage_multiplier sd_temp
scoreboard players operation #damage sd_temp /= #100 sd_const

# 3. 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
function stardew:combat/calculate_crit
function stardew:combat/apply_crit_damage

# 3.1 【泰坦之怒】伤害加成（银河+30%，无限+50%）
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players set #130 sd_const 130
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players operation #damage sd_temp *= #130 sd_const
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players set #150 sd_const 150
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players operation #damage sd_temp *= #150 sd_const
execute if entity @s[tag=sd_titan_wrath] run scoreboard players set #100 sd_const 100
execute if entity @s[tag=sd_titan_wrath] run scoreboard players operation #damage sd_temp /= #100 sd_const

# 4. 对所有目标应用伤害和击退
execute as @e[tag=sd_heavy_charge_target] at @s run function stardew:combat/weapon/heavy_charge_damage

# 5. 震地特效
particle minecraft:explosion ^ ^0.5 ^2.5 1 0.5 1 0 10 force
particle minecraft:cloud ^ ^0.5 ^2.5 1.5 0.5 1.5 0.1 30 force
particle minecraft:block{block_state:"minecraft:stone"} ^ ^0.5 ^2.5 1.5 0.5 1.5 0.2 50 force
particle minecraft:sweep_attack ^ ^1 ^2.5 1 0.5 1 0 5 force

# 6. 音效（沉重的锤击）
playsound minecraft:entity.zombie.break_wooden_door player @a ~ ~ ~ 1.5 0.5
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 1.0 0.8
playsound minecraft:block.anvil.land player @a ~ ~ ~ 1.2 0.6
playsound minecraft:entity.player.attack.knockback player @a ~ ~ ~ 1.5 0.5

# 7. 玩家视觉震动（屏幕抖动效果 - 通过短暂后坐力模拟）
tp @s ~ ~0.1 ~

# 8. 显示提示（根据等级）
execute if score #skill_level sd_temp matches 1 run title @s subtitle {"text":"💥 蓄力重击！","color":"red","bold":true}
execute if score #skill_level sd_temp matches 2 run title @s subtitle {"text":"💥 蓄力重击 II！","color":"red","bold":true}
execute if score #skill_level sd_temp matches 3 run title @s subtitle {"text":"💥 蓄力重击 III！","color":"red","bold":true}
execute if score #skill_level sd_temp matches 4 run title @s subtitle {"text":"💥 蓄力重击 IV！","color":"red","bold":true}
execute if score #skill_level sd_temp matches 5.. run title @s subtitle {"text":"💥 蓄力重击 V！","color":"red","bold":true}
title @s times 0 20 10
title @s title {"text":""}

# 9. 清理目标标签
tag @e[tag=sd_heavy_charge_target] remove sd_heavy_charge_target
