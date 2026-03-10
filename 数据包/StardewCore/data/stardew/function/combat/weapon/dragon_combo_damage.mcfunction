# 龙牙连击伤害计算
# 伤害已在Hit函数计算好，存储在 #damage sd_temp 中
# 这个函数会被目标实体以 @s 身份执行

# 如果是怪物，扣除 sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 显示伤害数字
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "🐉"
data modify storage stardew:temp color set value "yellow"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 受击音效
playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1

# 伤害粒子
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force

# 小击退效果（向后上方）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.00012 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.00012 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.15
