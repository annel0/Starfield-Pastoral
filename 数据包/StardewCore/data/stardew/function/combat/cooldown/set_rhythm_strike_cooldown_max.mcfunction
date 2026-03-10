# 设置节奏打击技能冷却时间最大值（8秒=160 ticks）
scoreboard players set @s sd_skill_cooldown 160

# 移除旧的bossbar（如果存在）
bossbar remove stardew:rhythm_strike_cooldown

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:rhythm_strike_cooldown {"text":"⚡ 节奏打击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:rhythm_strike_cooldown color white
bossbar set stardew:rhythm_strike_cooldown max 160
bossbar set stardew:rhythm_strike_cooldown value 160
bossbar set stardew:rhythm_strike_cooldown players @s
bossbar set stardew:rhythm_strike_cooldown visible false
