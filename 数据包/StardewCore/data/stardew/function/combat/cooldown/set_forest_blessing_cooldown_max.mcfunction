# 设置森林赐福技能冷却时间最大值（15秒=300 ticks）
scoreboard players set @s sd_skill_cooldown 300

# 移除旧的bossbar（如果存在）
bossbar remove stardew:forest_blessing_cooldown
bossbar remove stardew:forest_blessing_duration

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:forest_blessing_cooldown {"text":"🌿 森林赐福 - 冷却中","color":"gray","bold":true}
bossbar set stardew:forest_blessing_cooldown color white
bossbar set stardew:forest_blessing_cooldown max 300
bossbar set stardew:forest_blessing_cooldown value 300
bossbar set stardew:forest_blessing_cooldown players @s
bossbar set stardew:forest_blessing_cooldown visible false

# 创建持续时间bossbar（绿色，显示正常技能名）
bossbar add stardew:forest_blessing_duration {"text":"🌿 森林赐福","color":"#32CD32","bold":true}
bossbar set stardew:forest_blessing_duration color green
bossbar set stardew:forest_blessing_duration max 200
execute store result bossbar stardew:forest_blessing_duration value run scoreboard players get @s sd_regen_timer
bossbar set stardew:forest_blessing_duration players @s
bossbar set stardew:forest_blessing_duration visible true
