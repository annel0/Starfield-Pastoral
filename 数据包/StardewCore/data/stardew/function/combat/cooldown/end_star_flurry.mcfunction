# 星流连斩技能冷却结束

# 隐藏 Boss 血条
bossbar set stardew:star_flurry_cooldown visible false
bossbar set stardew:star_flurry_cooldown players

# 移除技能标签
tag @s remove sd_using_star_flurry

# 播放提示音效
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
