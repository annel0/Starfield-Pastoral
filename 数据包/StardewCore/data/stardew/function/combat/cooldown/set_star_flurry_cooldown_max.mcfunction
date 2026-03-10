# 设置星流连斩技能冷却时间最大值（6秒=120 ticks）
scoreboard players set @s sd_skill_cooldown 120

# 移除旧的bossbar（如果存在）
bossbar remove stardew:star_flurry_cooldown

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:star_flurry_cooldown {"text":"✨ 星流连斩 - 冷却中","color":"gray","bold":true}
bossbar set stardew:star_flurry_cooldown color white
bossbar set stardew:star_flurry_cooldown max 120
bossbar set stardew:star_flurry_cooldown value 120
bossbar set stardew:star_flurry_cooldown players @s
bossbar set stardew:star_flurry_cooldown visible false
