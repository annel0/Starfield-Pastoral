# 泰坦之怒结束

# 移除怒气标签
tag @s remove sd_titan_wrath

# 重置计时器
scoreboard players set @s sd_wrath_timer 0

# 播放结束音效
playsound minecraft:block.beacon.deactivate player @s ~ ~ ~ 1 1.2

# 结束粒子效果
particle minecraft:smoke ~ ~1 ~ 0.5 0.5 0.5 0.1 30 force
particle minecraft:large_smoke ~ ~1 ~ 0.3 0.5 0.3 0.05 15 force

# 提示
title @s actionbar {"text":"⚡ 泰坦之怒结束","color":"gray"}
