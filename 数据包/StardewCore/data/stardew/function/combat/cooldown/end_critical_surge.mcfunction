# 暴击涌动技能冷却结束

# 隐藏 Boss 血条
bossbar set stardew:critical_surge_cooldown visible false
bossbar set stardew:critical_surge_cooldown players

# 移除技能标签
tag @s remove sd_using_critical_surge

# 播放提示音效
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2

