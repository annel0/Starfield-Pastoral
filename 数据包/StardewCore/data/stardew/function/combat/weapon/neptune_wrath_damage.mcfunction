# 海王怒涛伤害应用
# 对单个敌人造成伤害、击退和减速效果

# 应用伤害
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "🌊"
data modify storage stardew:temp color set value "#00CED1"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 击退效果（海浪冲击 - 强力向后推飞）
execute store result score #motion_x sd_temp run data get entity @s Pos[0] 1000
execute store result score #motion_z sd_temp run data get entity @s Pos[2] 1000
execute store result score #player_x sd_temp run data get entity @p[tag=sd_using_neptune_wrath] Pos[0] 1000
execute store result score #player_z sd_temp run data get entity @p[tag=sd_using_neptune_wrath] Pos[2] 1000
scoreboard players operation #motion_x sd_temp -= #player_x sd_temp
scoreboard players operation #motion_z sd_temp -= #player_z sd_temp
execute store result entity @s Motion[0] double 0.0004 run scoreboard players get #motion_x sd_temp
execute store result entity @s Motion[2] double 0.0004 run scoreboard players get #motion_z sd_temp
data modify entity @s Motion[1] set value 0.5

# 缓流效果（减速30%，持续3秒）
effect give @s minecraft:slowness 3 0 false

# 粒子效果
particle minecraft:falling_water ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:splash ~ ~0.5 ~ 0.5 0.3 0.5 0.1 15 force

# 音效
playsound minecraft:entity.player.splash hostile @a ~ ~ ~ 1 1.2
