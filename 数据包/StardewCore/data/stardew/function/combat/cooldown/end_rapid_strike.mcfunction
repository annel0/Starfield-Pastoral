# 连击冷却结束

# 隐藏bossbar
bossbar set stardew:rapid_strike_cooldown visible false
bossbar set stardew:rapid_strike_cooldown players

# 移除标记
tag @s remove sd_using_rapid_strike

# 音效提示
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 0.5 2
