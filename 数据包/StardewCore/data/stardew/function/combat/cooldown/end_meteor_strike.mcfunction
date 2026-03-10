# 陨星打击技能冷却结束

# 隐藏bossbar
bossbar set stardew:meteor_strike_cooldown visible false
bossbar set stardew:meteor_strike_cooldown players

# 移除标签
tag @s remove sd_using_meteor_strike

# 播放提示音
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
