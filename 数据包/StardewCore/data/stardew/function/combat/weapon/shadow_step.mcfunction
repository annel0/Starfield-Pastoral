# 暗影步技能 (Shadow Step)
# 瞬移到敌人背后并造成伤害

# 检查是否在冷却中
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 先标记前方的敌人，检查是否有目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3,limit=1,sort=nearest] add sd_shadow_step_target

# 如果没有目标，提示并返回（不进入冷却）
execute unless entity @e[tag=sd_shadow_step_target] run tellraw @s {"text":"⚠ 附近没有目标！","color":"yellow"}
execute unless entity @e[tag=sd_shadow_step_target] run return 0

# 计算传送位置：以怪物为中心，面向玩家方向的反方向1.5格
# 使用玩家当前Y坐标，只改变X和Z
execute as @e[tag=sd_shadow_step_target] at @s facing entity @p feet positioned ^ ^ ^-1.5 run summon minecraft:marker ~ ~ ~ {Tags:["sd_teleport_marker"]}

# 检测传送位置是否安全
# 条件：脚部和身体是空气，脚下有方块支撑
execute as @e[type=minecraft:marker,tag=sd_teleport_marker] at @s if block ~ ~ ~ minecraft:air if block ~ ~1 ~ minecraft:air run tag @s add sd_safe_marker

# 如果位置不安全，取消并提示（不进入冷却）
execute unless entity @e[tag=sd_safe_marker] run tellraw @s {"text":"⚠ 目标位置被阻挡！","color":"yellow"}
execute unless entity @e[tag=sd_safe_marker] run kill @e[type=minecraft:marker,tag=sd_teleport_marker]
execute unless entity @e[tag=sd_safe_marker] run tag @e[tag=sd_shadow_step_target] remove sd_shadow_step_target
execute unless entity @e[tag=sd_safe_marker] run return 0

# 有目标且位置安全时才进入冷却
execute store result score @s sd_skill_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_cooldown
execute unless score @s sd_skill_cooldown matches 1.. run scoreboard players set @s sd_skill_cooldown 30
function stardew:combat/cooldown/set_shadow_step_cooldown_max

# 标记正在使用暗影步技能冷却
tag @s add sd_using_shadow_step

# 瞬移前的效果
playsound minecraft:entity.enderman.teleport player @a ~ ~ ~ 1 0.8
particle minecraft:portal ~ ~1 ~ 0.3 0.5 0.3 0.5 50 force

# 计算基础伤害（100%）- 在传送前计算好
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const

# 瞬移到标记位置
execute as @e[type=minecraft:marker,tag=sd_safe_marker] at @s run tp @p ~ ~ ~


# 传送后立刻造成伤害（减少被打窗口）
execute as @e[tag=sd_shadow_step_target] at @s run function stardew:combat/weapon/shadow_step_damage

# 清理标记实体
kill @e[type=minecraft:marker,tag=sd_teleport_marker]

# 瞬移后的效果
playsound minecraft:entity.enderman.teleport player @a ~ ~ ~ 1 1.2
particle minecraft:portal ~ ~1 ~ 0.3 0.5 0.3 0.5 50 force
particle minecraft:smoke ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force

# 提示
title @s subtitle [{"text":"👻 暗影步","color":"#8B008B","bold":true},{"text":" - 瞬移突袭","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 清理标记
tag @e[tag=sd_shadow_step_target] remove sd_shadow_step_target
