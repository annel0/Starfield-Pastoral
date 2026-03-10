# 龙牙狂怒结束

# 移除狂怒标签
tag @s remove sd_dragon_fury

# 清除抗性效果（以防万一）
effect clear @s minecraft:resistance

# 重置计时器
scoreboard players set @s sd_fury_timer 0

# 播放结束音效
playsound minecraft:block.beacon.deactivate player @s ~ ~ ~ 0.8 1.5

# 结束粒子效果
particle minecraft:smoke ~ ~1 ~ 0.3 0.5 0.3 0.05 20 force

# 提示
title @s actionbar {"text":"🐉 龙牙狂怒结束","color":"gray"}

