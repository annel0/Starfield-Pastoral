# 设置精准打击冷却 bossbar 最大值

# 设置最大值
scoreboard players operation #precision_strike_cooldown_max sd_const = @s sd_skill_cooldown

# 移除旧的bossbar（如果存在）
bossbar remove stardew:precision_strike_cooldown
bossbar remove stardew:precision_strike_duration

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:precision_strike_cooldown {"text":"⚔ 精准打击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:precision_strike_cooldown color white
bossbar set stardew:precision_strike_cooldown style progress
execute store result bossbar stardew:precision_strike_cooldown max run scoreboard players get #precision_strike_cooldown_max sd_const
execute store result bossbar stardew:precision_strike_cooldown value run scoreboard players get #precision_strike_cooldown_max sd_const
bossbar set stardew:precision_strike_cooldown players @s
bossbar set stardew:precision_strike_cooldown visible false

# 创建持续时间bossbar（金色，显示正常技能名）
bossbar add stardew:precision_strike_duration {"text":"⚔ 精准打击","color":"gold","bold":true}
bossbar set stardew:precision_strike_duration color yellow
bossbar set stardew:precision_strike_duration style progress
execute store result bossbar stardew:precision_strike_duration max run scoreboard players get @s sd_precision_duration
bossbar set stardew:precision_strike_duration players @s
bossbar set stardew:precision_strike_duration visible true
