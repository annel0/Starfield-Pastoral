# 设置蓄力重击技能冷却时间最大值（10秒=200 ticks）
scoreboard players set @s sd_skill_cooldown 200

# 移除旧的bossbar（如果存在）
bossbar remove stardew:heavy_charge_cooldown

# 创建冷却bossbar（白色，显示"冷却中"）- 初始设为满值
bossbar add stardew:heavy_charge_cooldown {"text":"💥 蓄力重击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:heavy_charge_cooldown color white
bossbar set stardew:heavy_charge_cooldown max 200
bossbar set stardew:heavy_charge_cooldown value 200
bossbar set stardew:heavy_charge_cooldown players @s
bossbar set stardew:heavy_charge_cooldown visible false
