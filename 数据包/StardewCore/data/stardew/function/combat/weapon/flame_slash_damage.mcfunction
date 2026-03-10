# 火焰斩伤害计算
# 造成120%伤害 + 3秒燃烧

# 获取武器基础伤害
execute store result score #damage_min sd_temp run data get entity @p[tag=!sd_flame_target] SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @p[tag=!sd_flame_target] SelectedItem.components."minecraft:custom_data".weapon_damage_max

# 计算平均伤害
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 120%伤害
scoreboard players set #120 sd_const 120
scoreboard players set #100 sd_const 100
scoreboard players operation #damage sd_temp *= #120 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @p[tag=sd_using_flame_slash] SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
execute as @p[tag=sd_using_flame_slash] run function stardew:combat/calculate_crit
execute as @p[tag=sd_using_flame_slash] run function stardew:combat/apply_crit_damage

# 应用伤害（直接扣除怪物血量）
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "🔥"
data modify storage stardew:temp color set value "#FF6347"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 施加自定义燃烧效果（从武器读取配置）
tag @s add sd_burning
execute store result score @s sd_burning_damage run data get entity @p[tag=!sd_flame_target] SelectedItem.components."minecraft:custom_data".weapon_burn_damage
execute unless score @s sd_burning_damage matches 1.. run scoreboard players set @s sd_burning_damage 10
execute store result score @s sd_burning_timer run data get entity @p[tag=!sd_flame_target] SelectedItem.components."minecraft:custom_data".weapon_burn_duration
execute unless score @s sd_burning_timer matches 1.. run scoreboard players set @s sd_burning_timer 60

# 燃烧粒子效果
execute if score #is_critical sd_temp matches 1 run particle minecraft:crit ~ ~1 ~ 0.5 0.8 0.5 0.2 30 force
particle minecraft:flame ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
execute if score #is_critical sd_temp matches 1 run particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
execute unless score #is_critical sd_temp matches 1 run particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0 5 force

# 音效
execute if score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 1.2

# 击退效果（火焰斩的轻微击退 - 向后上方）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p[tag=sd_using_flame_slash] Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p[tag=sd_using_flame_slash] Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.00015 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.00015 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.2
