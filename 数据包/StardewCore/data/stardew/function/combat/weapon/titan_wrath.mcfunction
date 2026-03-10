# 泰坦之怒技能 (Titan Wrath)
# 持续时间内：+伤害加成、普攻AOE、击中回血（根据武器tier调整数值）

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 400
function stardew:combat/cooldown/set_titan_wrath_cooldown_max

# 标记正在使用泰坦之怒技能冷却
tag @s add sd_using_titan_wrath

# 播放音效
playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 1.5 0.8
playsound minecraft:entity.wither.spawn player @a ~ ~ ~ 1 1.5
playsound minecraft:block.beacon.activate player @a ~ ~ ~ 2 1.5
playsound minecraft:entity.lightning_bolt.thunder player @a ~ ~ ~ 0.8 1.8
playsound minecraft:item.trident.thunder player @a ~ ~ ~ 1 2

# 视觉效果 - 泰坦之力爆发
particle minecraft:explosion ~ ~1 ~ 1 1 1 0 5 force
particle minecraft:enchanted_hit ~ ~1 ~ 1 1 1 0.5 100 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 5 force
particle minecraft:lava ~ ~1 ~ 1 1 1 0.3 50 force
particle minecraft:flame ~ ~1 ~ 1 1 1 0.3 80 force
particle minecraft:large_smoke ~ ~1 ~ 1 1 1 0.2 60 force

# 地面能量环
particle minecraft:enchant ~ ~0.1 ~ 3 0 3 0 80 force
particle minecraft:reverse_portal ~ ~0.5 ~ 2 0.5 2 0.5 120 force
particle minecraft:soul_fire_flame ~ ~0.1 ~ 2 0 2 0.1 40 force

# 提示（根据tier）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run title @s subtitle [{"text":"⚡ 泰坦之怒","color":"#9933FF","bold":true},{"text":" - 10秒：+30%攻击、AOE攻击、击中回血","color":"gold"}]
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run title @s subtitle [{"text":"⚡ 泰坦之怒","color":"#FF00FF","bold":true},{"text":" - 15秒：+50%攻击、AOE攻击、击中回血","color":"gold"}]
title @s times 0 20 10
title @s title {"text":""}

# 设置怒气持续时间（银河=10秒200 ticks，无限=15秒300 ticks）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players set @s sd_wrath_timer 200
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players set @s sd_wrath_timer 300

# 标记怒气状态
tag @s add sd_titan_wrath

# 应用效果
function stardew:combat/weapon/titan_wrath_apply
