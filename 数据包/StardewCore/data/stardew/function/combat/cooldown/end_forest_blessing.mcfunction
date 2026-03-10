# 森林赐福技能冷却结束

# 隐藏 Boss 血条
bossbar set stardew:forest_blessing_cooldown visible false
bossbar set stardew:forest_blessing_cooldown players

# 移除技能标签
tag @s remove sd_using_forest_blessing

# 播放提示音效
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
