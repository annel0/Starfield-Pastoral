# 蓄力重击伤害计算
# 伤害已在主函数计算好，存储在 #damage sd_temp 中

# 如果是怪物，扣除 sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 显示伤害数字
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "💥"
data modify storage stardew:temp color set value "red"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 受击音效
playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1
playsound minecraft:entity.item.break hostile @a ~ ~ ~ 1 0.8

# 伤害粒子
particle minecraft:damage_indicator ~ ~1 ~ 0.5 0.5 0.5 0.2 20 force
particle minecraft:crit ~ ~1 ~ 0.5 0.5 0.5 0.3 15 force

# 大范围击退（向外推+向上）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.0003 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.0003 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.6

# 眩晕效果（缓慢 II，持续2秒）
effect give @s minecraft:slowness 2 1 false
