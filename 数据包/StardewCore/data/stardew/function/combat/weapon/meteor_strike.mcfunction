# 陨星打击技能 (Meteor Strike)
# 跳跃砸地造成AOE攻击和眩晕（根据武器等级调整伤害）
# 【要求】：玩家必须在空中且正在下落才能触发

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 检查玩家是否在地面上（如果在地面，无法使用）
execute if data entity @s {OnGround:1b} run tellraw @s {"text":"✘ 陨星打击需要在空中使用！","color":"red","bold":true}
execute if data entity @s {OnGround:1b} run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.5 0.5
execute if data entity @s {OnGround:1b} run return 0

# 检查玩家是否在下落（Motion[1] < 0）
execute store result score #player_motion_y sd_temp run data get entity @s Motion[1] 1000
execute if score #player_motion_y sd_temp matches 0.. run tellraw @s {"text":"✘ 陨星打击需要在下落时使用！","color":"red","bold":true}
execute if score #player_motion_y sd_temp matches 0.. run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.5 0.5
execute if score #player_motion_y sd_temp matches 0.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 240
function stardew:combat/cooldown/set_meteor_strike_cooldown_max

# 标记正在使用陨星打击技能
tag @s add sd_using_meteor_strike

# 计算基础伤害（根据武器tier：银河=300%，无限=500%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 根据武器tier应用伤害倍率（银河=300%，无限=500%）
execute store result score #weapon_tier sd_temp run clear @s minecraft:carrot_on_a_stick 0
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players set #damage_multiplier sd_temp 300
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players set #damage_multiplier sd_temp 500
execute unless score #damage_multiplier sd_temp matches 1.. run scoreboard players set #damage_multiplier sd_temp 300
scoreboard players operation #damage sd_temp *= #damage_multiplier sd_temp
scoreboard players operation #damage sd_temp /= #100 sd_const

# 【泰坦之怒】伤害加成（银河+30%，无限+50%）
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players set #130 sd_const 130
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players operation #damage sd_temp *= #130 sd_const
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players set #150 sd_const 150
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players operation #damage sd_temp *= #150 sd_const
execute if entity @s[tag=sd_titan_wrath] run scoreboard players set #100 sd_const 100
execute if entity @s[tag=sd_titan_wrath] run scoreboard players operation #damage sd_temp /= #100 sd_const

# 音效（陨石坠落效果）
playsound minecraft:entity.wither.break_block player @a ~ ~ ~ 1.5 0.5
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 1.2 0.6
playsound minecraft:entity.lightning_bolt.thunder player @a ~ ~ ~ 0.8 1.2
playsound minecraft:block.anvil.land player @a ~ ~ ~ 1.0 0.8
playsound minecraft:item.trident.thunder player @a ~ ~ ~ 0.6 1.5

# 粒子效果（陨石冲击）
particle minecraft:explosion ~ ~0.5 ~ 2 0.5 2 0.2 15 force
particle minecraft:lava ~ ~0.5 ~ 3 0.3 3 0.1 40 force
particle minecraft:flame ~ ~1 ~ 3 1 3 0.3 100 force
particle minecraft:large_smoke ~ ~1 ~ 2 0.5 2 0.1 50 force
particle minecraft:block{block_state:"minecraft:magma_block"} ~ ~0.1 ~ 3 0.1 3 0.5 80 force
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run particle minecraft:flash ~ ~0.5 ~ 0 0 0 0 3 force
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run particle minecraft:enchanted_hit ~ ~1 ~ 4 1 4 0.3 80 force

# 对周围敌人造成伤害（范围：银河=5格，无限=7格）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} as @e[tag=sd_monster,distance=..5] at @s run function stardew:combat/weapon/meteor_strike_damage
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} as @e[tag=sd_monster,distance=..7] at @s run function stardew:combat/weapon/meteor_strike_damage

# 显示副标题（根据tier）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run title @s subtitle {"text":"☄ 陨星打击！","color":"#9933FF","bold":true}
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run title @s subtitle {"text":"☄ 陨星打击！","color":"#FF00FF","bold":true}
title @s times 0 20 10
title @s title ""
