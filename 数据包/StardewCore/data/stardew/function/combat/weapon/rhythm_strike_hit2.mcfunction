# 节奏打击 - 第二击（150%伤害）

# 1. 更新状态
tag @s remove sd_rhythm_1
tag @s remove sd_rhythm_window
tag @s add sd_rhythm_2

# 2. 计算攻击冷却时间
execute store result score #weapon_speed sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_attack_speed 10
execute unless score #weapon_speed sd_temp matches 1.. run scoreboard players set #weapon_speed sd_temp 8
scoreboard players set #cooldown_ticks sd_temp 200
scoreboard players operation #cooldown_ticks sd_temp /= #weapon_speed sd_temp
scoreboard players operation @s sd_rhythm_strike_timer = #cooldown_ticks sd_temp

# 3. 更新Bossbar为黄色倒计时
bossbar set stardew:rhythm_bar name {"text":"⚡ 节奏打击 II - 等待...","color":"yellow","bold":true}
bossbar set stardew:rhythm_bar color yellow
bossbar set stardew:rhythm_bar style notched_6
execute store result bossbar stardew:rhythm_bar max run scoreboard players get @s sd_rhythm_strike_timer
execute store result bossbar stardew:rhythm_bar value run scoreboard players get @s sd_rhythm_strike_timer

# 4. 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_rhythm_target

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
execute as @e[tag=sd_rhythm_target,limit=1] at @s run function stardew:combat/weapon/rhythm_strike_damage

# 8. 音效粒子（第二击，稍微不同）
playsound minecraft:entity.player.attack.strong player @a ~ ~ ~ 1.0 1.4
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.7 1.9
particle minecraft:crit ^ ^1 ^1.5 0.4 0.4 0.4 0.1 10 force
particle minecraft:sweep_attack ^ ^1 ^1.5 0.3 0.3 0.3 0 3 force

# 9. 清理目标标签
tag @e[tag=sd_rhythm_target] remove sd_rhythm_target
