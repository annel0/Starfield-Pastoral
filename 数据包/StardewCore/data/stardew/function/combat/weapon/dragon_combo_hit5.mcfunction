# 龙牙连击 - 终结技（300%伤害）

# 1. 清除所有标签
tag @s remove sd_dragon_combo_4
tag @s remove sd_dragon_combo_window
scoreboard players set @s sd_dragon_combo_timer 0

# 2. 移除Bossbar
bossbar set stardew:dragon_combo_bar visible false
bossbar remove stardew:dragon_combo_bar

# 3. 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_dragon_combo_target

# 4. 计算伤害（300%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #300 sd_const 300
scoreboard players operation #damage sd_temp *= #300 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 5. 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
function stardew:combat/calculate_crit
function stardew:combat/apply_crit_damage

# 6. 应用伤害
execute as @e[tag=sd_dragon_combo_target,limit=1] at @s run function stardew:combat/weapon/dragon_combo_damage

# 7. 终结技特效
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.0 2.0
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 1.0 2
playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 0.5 2.0
particle minecraft:sweep_attack ^ ^1 ^1.5 0.3 0.3 0.3 0 8 force
particle minecraft:dragon_breath ^ ^1 ^1.5 0.5 0.5 0.5 0.1 30 force
particle minecraft:flash ^ ^1 ^1.5 0 0 0 0 1 force

# 8. 清理目标标签
tag @e[tag=sd_dragon_combo_target] remove sd_dragon_combo_target

# 9. 进入技能冷却
function stardew:combat/cooldown/set_dragon_combo_cooldown_max
