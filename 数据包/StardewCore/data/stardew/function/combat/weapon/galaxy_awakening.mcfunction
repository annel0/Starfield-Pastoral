# 银河觉醒技能 (Galaxy Awakening)
# 6秒内：+60%暴击率，+30%移速（速度II），+15%闪避率

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 300
function stardew:combat/cooldown/set_galaxy_awakening_cooldown_max

# 标记正在使用银河觉醒技能冷却
tag @s add sd_using_galaxy_awakening

# 播放音效
playsound minecraft:block.beacon.activate player @a ~ ~ ~ 1.5 1.5
playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 0.8 2
playsound minecraft:block.end_portal.spawn player @a ~ ~ ~ 1 1.8
playsound minecraft:entity.wither.spawn player @a ~ ~ ~ 0.5 2

# 视觉效果 - 银河爆发
particle minecraft:dragon_breath ~ ~1 ~ 0.8 0.8 0.8 0.3 80 force
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 1 100 force
particle minecraft:end_rod ~ ~1 ~ 0.6 0.6 0.6 0.3 60 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 3 force
particle minecraft:firework ~ ~1 ~ 0.5 0.5 0.5 0.2 40 force

# 地面震波效果
particle minecraft:enchant ~ ~0.1 ~ 2 0 2 0 50 force
particle minecraft:reverse_portal ~ ~0.5 ~ 1.5 0.5 1.5 0.3 80 force

# 提示
title @s subtitle [{"text":"✨ 银河觉醒","color":"#9933FF","bold":true},{"text":" - 6秒：+60%暴击，+30%移速，+15%闪避","color":"gray"}]
title @s times 0 20 10
title @s title {"text":""}

# 设置觉醒持续时间（6秒 = 120 ticks）
scoreboard players set @s sd_awakening_timer 120

# 标记觉醒状态
tag @s add sd_galaxy_awakened

# 应用效果
function stardew:combat/weapon/galaxy_awakening_apply
