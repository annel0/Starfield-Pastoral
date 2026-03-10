# 设置骨裂打击技能冷却时间最大值

# 移除旧bossbar
bossbar remove stardew:bone_break_cooldown

# 创建冷却bossbar（灰色）
bossbar add stardew:bone_break_cooldown {"text":"💀 骨裂打击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:bone_break_cooldown color white
bossbar set stardew:bone_break_cooldown max 160
bossbar set stardew:bone_break_cooldown value 160
bossbar set stardew:bone_break_cooldown players @s
bossbar set stardew:bone_break_cooldown visible false
