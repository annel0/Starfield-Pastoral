# 设置突刺技能冷却时间最大值（4秒=80 ticks）
scoreboard players set @s sd_skill_cooldown 80

# 移除旧的bossbar（如果存在）
bossbar remove stardew:dash_strike_cooldown

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:dash_strike_cooldown {"text":"⚔ 突刺 - 冷却中","color":"gray","bold":true}
bossbar set stardew:dash_strike_cooldown color white
bossbar set stardew:dash_strike_cooldown max 80
bossbar set stardew:dash_strike_cooldown value 80
bossbar set stardew:dash_strike_cooldown players @s
bossbar set stardew:dash_strike_cooldown visible false
