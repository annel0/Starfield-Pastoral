# 旋风斩技能冷却结束

# 隐藏 Boss 血条
bossbar set stardew:whirlwind_cooldown visible false
bossbar set stardew:whirlwind_cooldown players

# 移除技能标签
tag @s remove sd_using_whirlwind

# 播放提示音效
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
