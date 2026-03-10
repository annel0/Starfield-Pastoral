# 星辰冲击造成伤害（使用预计算的#damage）

# 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @p[tag=sd_using_stellar] SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
execute as @p[tag=sd_using_stellar] run function stardew:combat/calculate_crit
execute as @p[tag=sd_using_stellar] run function stardew:combat/apply_crit_damage

# 对怪物扣除血量（使用sd_monster_hp系统）
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "⭐"
data modify storage stardew:temp color set value "#9933FF"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 对其他生物（使用原版damage命令）
execute unless entity @s[tag=sd_monster] run damage @s 1 minecraft:magic by @p

# 击中效果（区分暴击）
execute if score #is_critical sd_temp matches 1 run particle minecraft:crit ~ ~1 ~ 0.5 0.8 0.5 0.2 30 force
particle minecraft:explosion ~ ~1 ~ 0.2 0.3 0.2 0 3 force
particle minecraft:end_rod ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force
particle minecraft:soul_fire_flame ~ ~1 ~ 0.2 0.3 0.2 0.05 5 force
execute if score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 1.2
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 0.5 1.5
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 0.8 0.8

# 特殊技能 - 星辰冲击的超强击退（爆炸效果）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p[tag=sd_using_stellar] Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p[tag=sd_using_stellar] Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.00035 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.00035 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.45

# 清除标记
tag @s remove sd_stellar_hit
