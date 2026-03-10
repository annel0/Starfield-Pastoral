# 突刺伤害计算
# 对被标记的敌人造成150%武器伤害
# 伤害已经在主函数计算好，存储在 #damage sd_temp 中

# 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @p[tag=sd_using_dash] SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
execute as @p[tag=sd_using_dash] run function stardew:combat/calculate_crit
execute as @p[tag=sd_using_dash] run function stardew:combat/apply_crit_damage

# 如果是怪物，扣除 sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_hp sd_temp = @s sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_max_hp sd_temp = @s sd_monster_max_hp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "⚔"
data modify storage stardew:temp color set value "aqua"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 粒子效果和音效（区分暴击）- 减少粒子数量
execute if score #is_critical sd_temp matches 1 run particle minecraft:crit ~ ~1 ~ 0.5 0.8 0.5 0.2 40 force
execute if score #is_critical sd_temp matches 1 run particle minecraft:enchanted_hit ~ ~1 ~ 0.4 0.6 0.4 0.15 30 force
execute unless score #is_critical sd_temp matches 1 run particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 5 force
execute unless score #is_critical sd_temp matches 1 run particle minecraft:crit ~ ~1 ~ 0.4 0.6 0.4 0.15 25 force
particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 3 force
execute if score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 1.2
execute unless score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1 1.2
playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1

# 击退效果（向后上方推）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p[tag=sd_using_dash] Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p[tag=sd_using_dash] Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.0002 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.0002 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.25
