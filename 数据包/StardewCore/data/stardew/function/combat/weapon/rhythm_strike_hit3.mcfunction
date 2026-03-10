# 节奏打击 - 第三击（终结技，200%伤害+击退）

# 1. 清理状态标签并标记第三段
tag @s remove sd_rhythm_2
tag @s remove sd_rhythm_window
tag @s add sd_rhythm_3
scoreboard players set @s sd_rhythm_strike_timer 0

# 2. 移除Bossbar
bossbar remove stardew:rhythm_bar

# 3. 触发技能冷却（8秒）
scoreboard players set @s sd_skill_cooldown 160
function stardew:combat/cooldown/set_rhythm_strike_cooldown_max

# 4. 标记正在使用节奏打击冷却
tag @s add sd_using_rhythm_strike

# 5. 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_rhythm_target

# 6. 计算伤害（200%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #200 sd_const 200
scoreboard players operation #damage sd_temp *= #200 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 7. 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
function stardew:combat/calculate_crit
function stardew:combat/apply_crit_damage

# 8. 应用伤害和大击退
execute as @e[tag=sd_rhythm_target,limit=1] at @s run scoreboard players operation @s[tag=sd_monster] sd_monster_hp -= #damage sd_temp
execute as @e[tag=sd_rhythm_target,limit=1] at @s run playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1
execute as @e[tag=sd_rhythm_target,limit=1] at @s run particle minecraft:damage_indicator ~ ~1 ~ 0.5 0.5 0.5 0.2 5 force

# 大击退效果（终结技，更强的击飞）
execute as @e[tag=sd_rhythm_target,limit=1] at @s store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute as @e[tag=sd_rhythm_target,limit=1] at @s store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @s Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute as @e[tag=sd_rhythm_target,limit=1] store result entity @s Motion[0] double 0.00025 run scoreboard players get #motion_x sd_temp
execute as @e[tag=sd_rhythm_target,limit=1] store result entity @s Motion[2] double 0.00025 run scoreboard players get #motion_z sd_temp
execute as @e[tag=sd_rhythm_target,limit=1] run data modify entity @s Motion[1] set value 0.5

# 9. 音效粒子（终结技，加强版）
playsound minecraft:entity.player.attack.knockback player @a ~ ~ ~ 1.2 1.0
playsound minecraft:item.trident.throw player @a ~ ~ ~ 0.8 0.8
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 1.0 2.0
playsound minecraft:block.note_block.bell player @s ~ ~ ~ 0.8 2.0
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.8 1.8
particle minecraft:damage_indicator ^ ^1 ^1.5 0.5 0.5 0.5 0.2 5 force
particle minecraft:enchanted_hit ^ ^1 ^1.5 0.5 0.5 0.5 0.3 20 force
particle minecraft:sweep_attack ^ ^1 ^1.5 0.5 0.5 0.5 0 5 force

# 10. 显示提示
title @s subtitle [{"text":"⚡ 节奏打击","color":"gold","bold":true},{"text":" - 完美连击！","color":"yellow"}]
title @s times 0 30 10
title @s title {"text":""}

# 11. 清理目标标签
tag @e[tag=sd_rhythm_target] remove sd_rhythm_target
