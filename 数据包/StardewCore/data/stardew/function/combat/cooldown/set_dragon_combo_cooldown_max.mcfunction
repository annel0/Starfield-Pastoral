# 设置龙牙连击技能冷却时间最大值（10秒=200 ticks）
scoreboard players set @s sd_dragon_combo_cooldown 200

# 移除旧的bossbar（如果存在）
bossbar remove stardew:dragon_combo_cooldown

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:dragon_combo_cooldown {"text":"🐉 龙牙连击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:dragon_combo_cooldown color white
bossbar set stardew:dragon_combo_cooldown max 200
bossbar set stardew:dragon_combo_cooldown value 200
bossbar set stardew:dragon_combo_cooldown players @s
bossbar set stardew:dragon_combo_cooldown visible false
