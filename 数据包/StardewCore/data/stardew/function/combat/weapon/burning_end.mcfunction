# 灼烧效果结束

# 移除灼烧标签
tag @s remove sd_burning

# 清除分数
scoreboard players set @s sd_burning_damage 0
scoreboard players set @s sd_burning_timer 0

# 结束粒子
particle minecraft:smoke ~ ~1 ~ 0.3 0.5 0.3 0.05 15 force
