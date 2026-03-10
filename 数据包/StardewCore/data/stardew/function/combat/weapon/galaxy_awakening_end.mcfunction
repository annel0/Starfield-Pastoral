# 银河觉醒结束

# 移除觉醒标签
tag @s remove sd_galaxy_awakened

# 重置增益效果
scoreboard players set @s sd_awakening_crit_bonus 0
scoreboard players set @s sd_dodge_chance 0

# 重置计时器
scoreboard players set @s sd_awakening_timer 0

# 播放结束音效
playsound minecraft:block.beacon.deactivate player @s ~ ~ ~ 0.8 1.5
playsound minecraft:entity.ender_dragon.flap player @s ~ ~ ~ 0.5 1.8

# 结束粒子效果
particle minecraft:reverse_portal ~ ~1 ~ 0.5 0.5 0.5 0.1 30 force

# 提示
title @s actionbar {"text":"✨ 银河觉醒结束","color":"gray"}
