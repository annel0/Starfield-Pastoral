# 毒刃技能 - 施加持续毒性伤害
# 100%武器伤害 + 6伤害/秒 × 6秒的DOT

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3,limit=1,sort=nearest] add sd_poison_strike_target

# 没有目标 - 不进入冷却
execute unless entity @e[tag=sd_poison_strike_target] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_poison_strike_target] run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 80
function stardew:combat/cooldown/set_poison_strike_cooldown_max

# 标记正在使用毒刃技能冷却
tag @s add sd_using_poison_strike

# 播放音效
playsound minecraft:entity.spider.hurt player @a ~ ~ ~ 1 0.8
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1 1.2

# 提示
title @s subtitle [{"text":"☠ 毒刃","color":"#32CD32","bold":true},{"text":" - 施加剧毒","color":"dark_green"}]
title @s times 0 20 10
title @s title {"text":""}

# Calculate base damage (100% weapon damage)
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 获取毒性数据
execute store result score #poison_damage sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_poison_damage
execute store result score #poison_duration sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_poison_duration
execute unless score #poison_damage sd_temp matches 1.. run scoreboard players set #poison_damage sd_temp 6
execute unless score #poison_duration sd_temp matches 1.. run scoreboard players set #poison_duration sd_temp 120

# 对敌人造成伤害并施加毒性
execute as @e[tag=sd_poison_strike_target] at @s run function stardew:combat/weapon/poison_strike_damage

# 粒子效果
particle minecraft:sweep_attack ^ ^1 ^2 0.5 0.5 0.5 0 3 force
particle minecraft:dust{color:[0.2,0.8,0.2],scale:1.5} ^ ^1 ^2 0.5 0.8 0.5 0 30 force
particle minecraft:sneeze ~ ~1 ~ 0.3 0.5 0.3 0.05 15 force

# 清理标记
tag @e[tag=sd_poison_strike_target] remove sd_poison_strike_target
