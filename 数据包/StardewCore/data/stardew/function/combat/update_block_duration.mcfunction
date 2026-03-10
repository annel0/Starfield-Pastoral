# 更新格挡持续时间

# 更新持续时间bossbar
execute store result bossbar stardew:block_duration value run scoreboard players get @s sd_blocking

# 格挡结束 - 切换到冷却bossbar
execute if score @s sd_blocking matches ..0 run bossbar set stardew:block_duration visible false
execute if score @s sd_blocking matches ..0 store result bossbar stardew:block_cooldown value run scoreboard players get @s sd_block_cooldown
execute if score @s sd_blocking matches ..0 run bossbar set stardew:block_cooldown players @s
execute if score @s sd_blocking matches ..0 run bossbar set stardew:block_cooldown visible true
