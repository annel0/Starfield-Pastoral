# 骨裂打击技能 (Bone Break)
# 重击造成130%武器伤害，并对敌人施加减速效果

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 标记附近的目标（前方3格）
execute positioned ~ ~1.5 ~ positioned ^ ^ ^3 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_bone_break_check

# 没有目标 - 不进入冷却
execute unless entity @e[tag=sd_bone_break_check] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_bone_break_check] run return 0
tag @e[tag=sd_bone_break_check] remove sd_bone_break_check

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 160
function stardew:combat/cooldown/set_bone_break_cooldown_max

# 标记正在使用骨裂打击技能
tag @s add sd_using_bone_break

# 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^3 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..2,sort=nearest,limit=1] add sd_bone_break_target

# 计算基础伤害（130%）
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
# 应用130%伤害
scoreboard players set #130 sd_const 130
scoreboard players operation #damage sd_temp *= #130 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 音效（沉重的骨头打击声）
playsound minecraft:entity.skeleton.hurt player @a ~ ~ ~ 1.5 0.6
playsound minecraft:entity.player.attack.strong player @a ~ ~ ~ 1.2 0.8
playsound minecraft:entity.zombie.break_wooden_door player @a ~ ~ ~ 0.8 1.2

# 执行攻击
schedule function stardew:combat/weapon/bone_break_attack 1t

# 显示提示
title @s subtitle [{"text":"💀 骨裂打击","color":"#E0E0E0","bold":true},{"text":" - 130%伤害+减速","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}
