# 设置星辰冲击技能冷却时间最大值（7秒=140 ticks）
scoreboard players set @s sd_skill_2_cooldown 140

# 移除旧的bossbar（如果存在）
bossbar remove stardew:stellar_impact_cooldown

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:stellar_impact_cooldown {"text":"⭐ 星辰冲击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:stellar_impact_cooldown color white
bossbar set stardew:stellar_impact_cooldown max 140
bossbar set stardew:stellar_impact_cooldown value 140
bossbar set stardew:stellar_impact_cooldown players @s
bossbar set stardew:stellar_impact_cooldown visible false
