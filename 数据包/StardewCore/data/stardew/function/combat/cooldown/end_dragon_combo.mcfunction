# 龙牙连击技能冷却结束

# 隐藏bossbar
bossbar set stardew:dragon_combo_cooldown visible false
bossbar set stardew:dragon_combo_cooldown players

# 移除标签
tag @s remove sd_using_dragon_combo

# 播放提示音
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
