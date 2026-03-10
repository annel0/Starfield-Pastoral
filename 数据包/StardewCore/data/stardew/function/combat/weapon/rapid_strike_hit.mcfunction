# 执行单次连击攻击

# 获取武器攻击距离
execute store result score #weapon_range sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_range 10
execute unless score #weapon_range sd_temp matches 1.. run scoreboard players set #weapon_range sd_temp 30

# 标记前方的敌人（使用射线，但只标记不造成伤害）
scoreboard players set #ray_distance sd_temp 0
execute anchored eyes positioned ^ ^ ^ run function stardew:combat/weapon/rapid_strike_raycast

# 计算伤害（60%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #60 sd_const 60
scoreboard players operation #damage sd_temp *= #60 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 音效和粒子
playsound minecraft:entity.player.attack.strong player @a ~ ~ ~ 1 1.8
particle minecraft:sweep_attack ^ ^1 ^2 0.5 0.5 0.5 0.1 8
particle minecraft:crit ^ ^1 ^2 0.4 0.4 0.4 0.2 15

# 对目标造成伤害（只对被标记的敌人）
execute as @e[tag=sd_rapid_strike_target] at @s run function stardew:combat/weapon/rapid_strike_damage

# 清理标记
tag @e[tag=sd_rapid_strike_target] remove sd_rapid_strike_target
