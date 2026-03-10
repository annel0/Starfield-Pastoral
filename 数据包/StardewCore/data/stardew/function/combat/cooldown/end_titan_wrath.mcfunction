# 泰坦之怒技能冷却结束

# 隐藏bossbar
bossbar set stardew:titan_wrath_cooldown visible false
bossbar set stardew:titan_wrath_cooldown players

# 移除标签
tag @s remove sd_using_titan_wrath

# 播放提示音
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2

# 提示
title @s actionbar {"text":"✅ 泰坦之怒已就绪！","color":"green"}
