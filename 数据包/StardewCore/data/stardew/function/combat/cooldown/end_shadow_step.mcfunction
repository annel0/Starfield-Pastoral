# 暗影步技能冷却结束

# 重置冷却时间
scoreboard players set @s sd_skill_cooldown 0

# 移除标记
tag @s remove sd_using_shadow_step

# 移除冷却条
bossbar remove stardew:shadow_step_cooldown

# 播放提示音效
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 1 2

# 提示
title @s actionbar {"text":"✓ 暗影步技能已就绪","color":"green","bold":true}
