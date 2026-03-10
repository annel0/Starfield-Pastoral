# 震地重击技能冷却结束

# 隐藏bossbar
bossbar set stardew:ground_slam_cooldown visible false
bossbar set stardew:ground_slam_cooldown players

# 移除标签
tag @s remove sd_using_ground_slam

# 播放提示音
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
