# 剧毒爆发 - 引爆所有剧毒效果
# 150%武器伤害 + 50%剩余毒性伤害

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 标记范围内的中毒敌人
tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..5,tag=sd_poisoned] add sd_poison_burst_target

# 没有中毒目标 - 不进入冷却
execute unless entity @e[tag=sd_poison_burst_target] run tellraw @s {"text":"⚠ 附近没有中毒的敌人！","color":"yellow"}
execute unless entity @e[tag=sd_poison_burst_target] run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 200
function stardew:combat/cooldown/set_poison_burst_cooldown_max

# 标记正在使用剧毒爆发技能冷却
tag @s add sd_using_poison_burst

# 播放音效
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.spider.death player @a ~ ~ ~ 1 0.8

# 提示
title @s subtitle [{"text":"💥 剧毒爆发","color":"#32CD32","bold":true},{"text":" - 引爆毒性","color":"dark_green"}]
title @s times 0 20 10
title @s title {"text":""}

# Calculate base damage (150% weapon damage)
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #150 sd_const 150
scoreboard players operation #damage sd_temp *= #150 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 对每个中毒敌人：先计算引爆伤害，然后以其为中心对周围3格敌人造成伤害
execute as @e[tag=sd_poison_burst_target] at @s run function stardew:combat/weapon/poison_burst_explode

# 粒子效果
execute as @e[tag=sd_poison_burst_target] at @s run particle minecraft:explosion ~ ~1 ~ 0.5 0.5 0.5 0 3 force
execute as @e[tag=sd_poison_burst_target] at @s run particle minecraft:item_slime ~ ~1 ~ 1 1 1 0.3 50 force
particle minecraft:dust{color:[0.2,0.8,0.2],scale:3} ~ ~1 ~ 3 1 3 0 100 force

# 清理标记
tag @e[tag=sd_poison_burst_target] remove sd_poison_burst_target
