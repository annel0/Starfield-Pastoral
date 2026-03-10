# 暴击涌动结束

# 移除涌动标签（修复：使用正确的tag名）
tag @s remove sd_crit_surge_active

# 重置计时器
scoreboard players set @s sd_surge_timer 0

# 播放结束音效
playsound minecraft:block.beacon.deactivate player @s ~ ~ ~ 0.8 1.5

# 结束粒子效果
particle minecraft:smoke ~ ~1 ~ 0.3 0.5 0.3 0.05 20 force

# 提示
title @s actionbar {"text":"⚡ 暴击涌动结束","color":"gray"}


