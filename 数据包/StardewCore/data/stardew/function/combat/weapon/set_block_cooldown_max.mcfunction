# 设置格挡冷却最大值（使用宏）

# 移除旧的bossbar（如果存在）
bossbar remove stardew:block_cooldown
bossbar remove stardew:block_duration

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:block_cooldown {"text":"🛡 格挡 - 冷却中","color":"gray","bold":true}
bossbar set stardew:block_cooldown color white
$bossbar set stardew:block_cooldown max $(cooldown)
$bossbar set stardew:block_cooldown value $(cooldown)
bossbar set stardew:block_cooldown visible false

# 创建持续时间bossbar（蓝色，显示正常技能名）
bossbar add stardew:block_duration {"text":"🛡 格挡中","color":"aqua","bold":true}
bossbar set stardew:block_duration color blue
bossbar set stardew:block_duration max 60
bossbar set stardew:block_duration value 60
bossbar set stardew:block_duration players @s
bossbar set stardew:block_duration visible true
