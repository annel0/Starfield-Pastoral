# 龙牙狂怒技能冷却结束

# 隐藏bossbar
bossbar set stardew:dragon_fury_cooldown visible false
bossbar set stardew:dragon_fury_cooldown players

# 移除标签
tag @s remove sd_using_dragon_fury

# 播放提示音
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2

# 提示
title @s actionbar {"text":"✅ 龙牙狂怒已就绪！","color":"green"}
