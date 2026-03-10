# 毒刃技能冷却结束

# 隐藏 Boss 血条
bossbar set stardew:poison_strike_cooldown visible false
bossbar set stardew:poison_strike_cooldown players

# 移除技能标签
tag @s remove sd_using_poison_strike

# 播放提示音效
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
