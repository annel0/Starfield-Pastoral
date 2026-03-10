# 背刺伤害计算
# 对被标记的敌人必定造成暴击（300%伤害）
# 基础伤害已经在主函数计算好，存储在 #damage sd_temp 中

# 应用暴击倍率（300%）
scoreboard players set #300 sd_const 300
scoreboard players operation #damage sd_temp *= #300 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 如果是怪物，扣除 sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_hp sd_temp = @s sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_max_hp sd_temp = @s sd_monster_max_hp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "🗡"
data modify storage stardew:temp color set value "#8B4513"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 粒子效果和音效（暴击效果）
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:crit ~ ~1 ~ 0.5 0.8 0.5 0.2 40 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.4 0.6 0.4 0.15 25 force
particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 2 force
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 0.9
playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1.2 0.8

# 击退效果（背刺的强力击退 - 向后上方）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p[tag=sd_using_backstab] Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p[tag=sd_using_backstab] Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.00025 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.00025 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.35
