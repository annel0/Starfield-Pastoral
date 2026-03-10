# 龙牙狂怒技能 (Dragon Fury)
# 10秒内：+50%攻击力、-30%受伤、普攻AOE 3格范围

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 400
function stardew:combat/cooldown/set_dragon_fury_cooldown_max

# 标记正在使用龙牙狂怒技能冷却
tag @s add sd_using_dragon_fury

# 播放音效
playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 1 2
playsound minecraft:entity.blaze.shoot player @a ~ ~ ~ 1 1.5
playsound minecraft:block.beacon.activate player @a ~ ~ ~ 1.5 2
playsound minecraft:entity.wither.spawn player @a ~ ~ ~ 0.5 2

# 视觉效果 - 龙之力量爆发
particle minecraft:dragon_breath ~ ~1 ~ 1 1 1 0.5 100 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.8 0.8 0.8 0.3 60 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 3 force
particle minecraft:explosion ~ ~1 ~ 0.5 0.5 0.5 0 2 force
particle minecraft:flame ~ ~1 ~ 0.8 0.8 0.8 0.2 40 force

# 地面能量环
particle minecraft:enchant ~ ~0.1 ~ 2 0 2 0 50 force
particle minecraft:reverse_portal ~ ~0.5 ~ 1.5 0.5 1.5 0.3 80 force

# 提示
title @s subtitle [{"text":"� 龙牙狂怒","color":"#8B008B","bold":true},{"text":" - 10秒：+50%攻击、-30%受伤、普攻AOE","color":"gold"}]
title @s times 0 20 10
title @s title {"text":""}

# 设置狂怒持续时间（10秒 = 200 ticks）
scoreboard players set @s sd_fury_timer 200

# 标记狂怒状态
tag @s add sd_dragon_fury

# 应用效果
function stardew:combat/weapon/dragon_fury_apply
