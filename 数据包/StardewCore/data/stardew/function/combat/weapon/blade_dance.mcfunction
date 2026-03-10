# 刀锋之舞技能 (Blade Dance)
# 快速连续斩击5次，每次50%伤害（瞬发技能）

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 标记附近的目标（前方2.5格）
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_blade_dance_check

# 没有目标 - 不进入冷却
execute unless entity @e[tag=sd_blade_dance_check] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_blade_dance_check] run return 0
tag @e[tag=sd_blade_dance_check] remove sd_blade_dance_check

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 120
function stardew:combat/cooldown/set_blade_dance_cooldown_max

# 标记正在使用刀锋之舞技能冷却
tag @s add sd_using_blade_dance

# 标记目标（保留tag用于schedule）
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_blade_dance_target

# 计算基础伤害（30%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
# 应用30%伤害（5次共150%）
scoreboard players set #30 sd_const 30
scoreboard players operation #damage sd_temp *= #30 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 【重构】在这里统一计算暴击（只计算一次，所有hit共享）
execute store result score #weapon_crit sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
function stardew:combat/calculate_crit
function stardew:combat/apply_crit_damage

# 执行5次攻击（间隔3 ticks，更有打击感）
schedule function stardew:combat/weapon/blade_dance_attack1 1t
schedule function stardew:combat/weapon/blade_dance_attack2 4t
schedule function stardew:combat/weapon/blade_dance_attack3 7t
schedule function stardew:combat/weapon/blade_dance_attack4 10t
schedule function stardew:combat/weapon/blade_dance_attack5 13t

# 提示
title @s subtitle [{"text":"⚔ 刀锋之舞","color":"light_purple","bold":true},{"text":" - 5次连斩，每次30%伤害","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}
