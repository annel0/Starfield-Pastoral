# 无限觉醒技能 (Infinity Awakening)
# 8秒内：+80%暴击率，+40%移速（速度III），+25%闪避率，+30%攻速（急迫III）

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 400
function stardew:combat/cooldown/set_infinity_awakening_cooldown_max

# 标记正在使用无限觉醒技能冷却
tag @s add sd_using_infinity_awakening

# 播放音效
playsound minecraft:block.beacon.activate player @a ~ ~ ~ 2 2
playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 1 2
playsound minecraft:block.end_portal.spawn player @a ~ ~ ~ 1.5 2
playsound minecraft:entity.wither.spawn player @a ~ ~ ~ 1 2
playsound minecraft:item.trident.thunder player @a ~ ~ ~ 1 2

# 视觉效果 - 无限爆发（更强烈）
particle minecraft:dragon_breath ~ ~1 ~ 1 1 1 0.5 120 force
particle minecraft:portal ~ ~1 ~ 0.8 0.8 0.8 1.5 150 force
particle minecraft:end_rod ~ ~1 ~ 0.8 0.8 0.8 0.5 80 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 5 force
particle minecraft:firework ~ ~1 ~ 0.8 0.8 0.8 0.3 60 force
particle minecraft:explosion ~ ~1 ~ 0.5 0.5 0.5 0 3 force

# 地面震波效果
particle minecraft:enchant ~ ~0.1 ~ 2.5 0 2.5 0 80 force
particle minecraft:reverse_portal ~ ~0.5 ~ 2 0.5 2 0.5 120 force
particle minecraft:electric_spark ~ ~1 ~ 1 1 1 0.3 50 force

# 提示
title @s subtitle [{"text":"♾ 无限觉醒","color":"#FF33FF","bold":true},{"text":" - 8秒：+80%暴击，+40%移速，+25%闪避，+30%攻速","color":"gold"}]
title @s times 0 20 10
title @s title {"text":""}

# 设置觉醒持续时间（8秒 = 160 ticks）
scoreboard players set @s sd_awakening_timer 160

# 标记觉醒状态
tag @s add sd_infinity_awakened

# 应用效果
function stardew:combat/weapon/infinity_awakening_apply
