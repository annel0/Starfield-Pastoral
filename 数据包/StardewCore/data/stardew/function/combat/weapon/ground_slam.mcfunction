# 震地重击技能 (Ground Slam)
# 砸地造成AOE攻击，击退敌人（根据等级调整伤害）

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 获取技能等级
execute store result score #skill_level sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_level
execute unless score #skill_level sd_temp matches 1.. run scoreboard players set #skill_level sd_temp 1

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 200
function stardew:combat/cooldown/set_ground_slam_cooldown_max

# 标记正在使用震地重击技能
tag @s add sd_using_ground_slam

# 计算基础伤害（根据等级：150%/180%/210%/240%/270%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 根据技能等级应用伤害倍率
execute if score #skill_level sd_temp matches 1 run scoreboard players set #damage_multiplier sd_temp 150
execute if score #skill_level sd_temp matches 2 run scoreboard players set #damage_multiplier sd_temp 180
execute if score #skill_level sd_temp matches 3 run scoreboard players set #damage_multiplier sd_temp 210
execute if score #skill_level sd_temp matches 4 run scoreboard players set #damage_multiplier sd_temp 240
execute if score #skill_level sd_temp matches 5.. run scoreboard players set #damage_multiplier sd_temp 270
scoreboard players operation #damage sd_temp *= #damage_multiplier sd_temp
scoreboard players operation #damage sd_temp /= #100 sd_const

# 音效（根据等级增强）
execute if score #skill_level sd_temp matches ..2 run playsound minecraft:entity.wither.break_block player @a ~ ~ ~ 1 0.8
execute if score #skill_level sd_temp matches 3.. run playsound minecraft:entity.wither.break_block player @a ~ ~ ~ 1.2 0.7
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 0.5 0.5
execute if score #skill_level sd_temp matches 2.. run playsound minecraft:block.gravel.break player @a ~ ~ ~ 1.2 0.5
execute if score #skill_level sd_temp matches 3.. run playsound minecraft:block.anvil.land player @a ~ ~ ~ 0.8 1.5

# 粒子效果（根据等级增强）
execute if score #skill_level sd_temp matches 1 run particle minecraft:block{block_state:"minecraft:dirt"} ~ ~0.1 ~ 2 0.1 2 0.5 30 force
execute if score #skill_level sd_temp matches 2 run particle minecraft:block{block_state:"minecraft:dirt"} ~ ~0.1 ~ 2 0.1 2 0.5 40 force
execute if score #skill_level sd_temp matches 2 run particle minecraft:block{block_state:"minecraft:stone"} ~ ~0.1 ~ 2 0.1 2 0.5 20 force
execute if score #skill_level sd_temp matches 3.. run particle minecraft:block{block_state:"minecraft:dirt"} ~ ~0.1 ~ 2 0.1 2 0.5 50 force
execute if score #skill_level sd_temp matches 3.. run particle minecraft:block{block_state:"minecraft:stone"} ~ ~0.1 ~ 2 0.1 2 0.5 30 force
execute if score #skill_level sd_temp matches 3.. run particle minecraft:explosion ~ ~0.5 ~ 1.5 0.2 1.5 0.1 5 force
execute if score #skill_level sd_temp matches 4.. run particle minecraft:cloud ~ ~0.5 ~ 1.5 0.2 1.5 0.1 20 force

# 对周围敌人造成伤害（范围随等级增加：3/3.5/3.5/4/4格）
execute if score #skill_level sd_temp matches 1 as @e[tag=sd_monster,distance=..3] at @s run function stardew:combat/weapon/ground_slam_damage
execute if score #skill_level sd_temp matches 2..3 as @e[tag=sd_monster,distance=..3.5] at @s run function stardew:combat/weapon/ground_slam_damage
execute if score #skill_level sd_temp matches 4.. as @e[tag=sd_monster,distance=..4] at @s run function stardew:combat/weapon/ground_slam_damage

# 注意：不要在这里移除标记！标记要在冷却结束时移除（end_ground_slam.mcfunction）

# 显示副标题（根据等级）
execute if score #skill_level sd_temp matches 1 run title @s subtitle {"text":"震地重击！","color":"gold","bold":true}
execute if score #skill_level sd_temp matches 2 run title @s subtitle {"text":"震地重击 II！","color":"gold","bold":true}
execute if score #skill_level sd_temp matches 3 run title @s subtitle {"text":"震地重击 III！","color":"gold","bold":true}
execute if score #skill_level sd_temp matches 4 run title @s subtitle {"text":"震地重击 IV！","color":"gold","bold":true}
execute if score #skill_level sd_temp matches 5.. run title @s subtitle {"text":"震地重击 V！","color":"gold","bold":true}
title @s times 0 20 10
title @s title ""
