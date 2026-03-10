# 暴击涌动技能 (Critical Surge)
# 6秒内：+50%暴击率，暴击伤害从3倍提升到4倍

# 检查是否在冷却中
execute if score @s sd_skill_2_cooldown matches 1.. run return 0

# 获取技能冷却时间（从武器读取）
execute store result score @s sd_skill_2_cooldown run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_skill_2_cooldown
execute unless score @s sd_skill_2_cooldown matches 1.. run scoreboard players set @s sd_skill_2_cooldown 240
function stardew:combat/cooldown/set_critical_surge_cooldown_max

# 标记正在使用暴击涌动技能冷却
tag @s add sd_using_critical_surge

# 播放音效
playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 1 2
playsound minecraft:entity.blaze.shoot player @a ~ ~ ~ 1 1.5
playsound minecraft:block.beacon.activate player @a ~ ~ ~ 1.5 2
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 2 1

# 视觉效果 - 暴击能量爆发
particle minecraft:crit ~ ~1 ~ 1 1 1 0.5 100 force
particle minecraft:enchanted_hit ~ ~1 ~ 0.8 0.8 0.8 0.3 60 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 3 force
particle minecraft:explosion ~ ~1 ~ 0.5 0.5 0.5 0 2 force
particle minecraft:flame ~ ~1 ~ 0.8 0.8 0.8 0.2 40 force

# 地面能量环
particle minecraft:enchant ~ ~0.1 ~ 2 0 2 0 50 force
particle minecraft:reverse_portal ~ ~0.5 ~ 1.5 0.5 1.5 0.3 80 force

# 提示
title @s subtitle [{"text":"⚡ 暴击涌动","color":"#FFD700","bold":true},{"text":" - 6秒：+50%暴击率，暴击伤害×4","color":"gold"}]
title @s times 0 20 10
title @s title {"text":""}

# 设置涌动持续时间（6秒 = 120 ticks）
scoreboard players set @s sd_surge_timer 120

# 标记涌动状态（修复：使用正确的tag名）
tag @s add sd_crit_surge_active

# 应用效果
function stardew:combat/weapon/critical_surge_apply
