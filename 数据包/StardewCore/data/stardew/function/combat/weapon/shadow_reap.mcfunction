# 暗影收割 - 斩杀技能
# 对低血量目标造成巨额伤害

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 160
function stardew:combat/cooldown/set_shadow_reap_cooldown_max

# 标记正在使用暗影收割技能冷却
tag @s add sd_using_shadow_reap

# 播放音效
playsound minecraft:entity.wither.hurt player @a ~ ~ ~ 1 1.5
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1 0.8

# 提示
title @s subtitle [{"text":"💀 暗影收割","color":"#8B008B","bold":true},{"text":" - 200%伤害 斩杀翻倍","color":"dark_gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 标记目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^2.5 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3,limit=1,sort=nearest] add sd_shadow_reap_target

# Calculate base damage (200% weapon damage)
execute store result score #damage_min sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #damage_max sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
scoreboard players operation #damage sd_temp = #damage_min sd_temp
scoreboard players operation #damage sd_temp += #damage_max sd_temp
scoreboard players operation #damage sd_temp /= #2 sd_const
scoreboard players set #200 sd_const 200
scoreboard players operation #damage sd_temp *= #200 sd_const
scoreboard players operation #damage sd_temp /= #100 sd_const

# 对敌人造成伤害
execute as @e[tag=sd_shadow_reap_target] at @s run function stardew:combat/weapon/shadow_reap_damage

# 粒子效果
particle minecraft:sweep_attack ^ ^1 ^2 0.5 0.5 0.5 0 3 force
particle minecraft:dust{color:[0.2,0.0,0.2],scale:2} ^ ^1 ^2 0.8 0.8 0.8 0 30 force
particle minecraft:soul ~ ~1 ~ 0.3 0.5 0.3 0.05 20 force

# 清理标记
tag @e[tag=sd_shadow_reap_target] remove sd_shadow_reap_target
