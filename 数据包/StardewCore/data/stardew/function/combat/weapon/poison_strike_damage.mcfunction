# 毒刃伤害计算 - 施加即时伤害和DOT

# 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @p[tag=sd_using_poison_strike] SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
execute as @p[tag=sd_using_poison_strike] run function stardew:combat/calculate_crit
execute as @p[tag=sd_using_poison_strike] run function stardew:combat/apply_crit_damage

# 即时伤害
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 显示伤害数字
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "☠"
data modify storage stardew:temp color set value "#32CD32"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 施加毒性DOT
# 保存毒性伤害和持续时间到怪物身上
scoreboard players operation @s sd_poison_damage = #poison_damage sd_temp
scoreboard players operation @s sd_poison_timer = #poison_duration sd_temp
tag @s add sd_poisoned

# 视觉效果
damage @s 0 minecraft:generic by @p
execute if score #is_critical sd_temp matches 1 run particle minecraft:crit ~ ~1 ~ 0.5 0.8 0.5 0.2 30 force
execute unless score #is_critical sd_temp matches 1 run particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.3 5 force
particle minecraft:item_slime ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force

# 音效
execute if score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 1.2
execute unless score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1 1.2
playsound minecraft:entity.spider.ambient player @a ~ ~ ~ 1 0.8

# 击退效果（最轻击退 - 向后上方）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p[tag=sd_using_poison_strike] Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p[tag=sd_using_poison_strike] Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.0001 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.0001 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.12

