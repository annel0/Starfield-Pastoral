# 设置旋风斩技能冷却时间最大值（6秒=120 ticks）
scoreboard players set @s sd_skill_2_cooldown 120

# 移除旧的bossbar（如果存在）
bossbar remove stardew:whirlwind_cooldown

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:whirlwind_cooldown {"text":"� 旋风斩 - 冷却中","color":"gray","bold":true}
bossbar set stardew:whirlwind_cooldown color white
bossbar set stardew:whirlwind_cooldown max 120
bossbar set stardew:whirlwind_cooldown value 120
bossbar set stardew:whirlwind_cooldown players @s
bossbar set stardew:whirlwind_cooldown visible false
