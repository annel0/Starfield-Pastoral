# 设置星辰护盾技能冷却时间最大值（8秒=160 ticks）
scoreboard players set @s sd_skill_cooldown 160

# 移除旧的bossbar（如果存在）
bossbar remove stardew:astral_aegis_cooldown
bossbar remove stardew:astral_aegis_duration

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:astral_aegis_cooldown {"text":"🛡 星辰护盾 - 冷却中","color":"gray","bold":true}
bossbar set stardew:astral_aegis_cooldown color white
bossbar set stardew:astral_aegis_cooldown max 160
bossbar set stardew:astral_aegis_cooldown value 160
bossbar set stardew:astral_aegis_cooldown players @s
bossbar set stardew:astral_aegis_cooldown visible false

# 创建持续时间bossbar（紫色，显示正常技能名）
bossbar add stardew:astral_aegis_duration {"text":"🛡 星辰护盾","color":"#9933FF","bold":true}
bossbar set stardew:astral_aegis_duration color purple
bossbar set stardew:astral_aegis_duration max 100
execute store result bossbar stardew:astral_aegis_duration value run scoreboard players get @s sd_shield_timer
bossbar set stardew:astral_aegis_duration players @s
bossbar set stardew:astral_aegis_duration visible true
