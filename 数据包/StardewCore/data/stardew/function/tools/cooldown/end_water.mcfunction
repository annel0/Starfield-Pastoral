# data/stardew/functions/tools/cooldown/end_water.mcfunction
# 水壶冷却结束

# 隐藏 Boss 血条
bossbar set stardew:water_cooldown visible false

# 播放完成音效
playsound minecraft:block.brewing_stand.brew player @s ~ ~ ~ 0.7 1.8
