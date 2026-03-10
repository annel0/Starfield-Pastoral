# 龙牙连击 - 第二击（150%伤害）

# 1. 移除第一段标签，添加第二段标签
tag @s remove sd_dragon_combo_1
tag @s remove sd_dragon_combo_window
tag @s add sd_dragon_combo_2

# 2. 重新计算攻击冷却时间
execute store result score #weapon_speed sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_attack_speed 10
execute unless score #weapon_speed sd_temp matches 1.. run scoreboard players set #weapon_speed sd_temp 8
scoreboard players set #cooldown_ticks sd_temp 200
scoreboard players operation #cooldown_ticks sd_temp /= #weapon_speed sd_temp
scoreboard players operation @s sd_dragon_combo_timer = #cooldown_ticks sd_temp

# 3. 更新Bossbar
bossbar remove stardew:dragon_combo_bar
bossbar add stardew:dragon_combo_bar {"text":"🐉 龙牙连击 II - 等待...","color":"yellow","bold":true}
bossbar set stardew:dragon_combo_bar color yellow
bossbar set stardew:dragon_combo_bar style notched_6
execute store result bossbar stardew:dragon_combo_bar max run scoreboard players get @s sd_dragon_combo_timer
execute store result bossbar stardew:dragon_combo_bar value run scoreboard players get @s sd_dragon_combo_timer
bossbar set stardew:dragon_combo_bar players @s
bossbar set stardew:dragon_combo_bar visible true

# 4. 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_dragon_combo_target

# 5. 计算伤害（150%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #150 sd_const 150
scoreboard players operation #damage sd_temp *= #150 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 6. 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
function stardew:combat/calculate_crit
function stardew:combat/apply_crit_damage

# 7. 应用伤害
execute as @e[tag=sd_dragon_combo_target,limit=1] at @s run function stardew:combat/weapon/dragon_combo_damage

# 8. 音效粒子
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.0 1.7
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.7 1.9
particle minecraft:sweep_attack ^ ^1 ^1.5 0.3 0.3 0.3 0 4 force

# 9. 清理目标标签
tag @e[tag=sd_dragon_combo_target] remove sd_dragon_combo_target
