# 龙牙狂怒持续效果处理

# 每5 ticks粒子效果
scoreboard players operation #particle_check sd_temp = @s sd_fury_timer
scoreboard players set #5 sd_const 5
scoreboard players operation #particle_check sd_temp %= #5 sd_const
execute if score #particle_check sd_temp matches 0 run particle minecraft:dragon_breath ~ ~1 ~ 0.3 0.5 0.3 0.1 5 force
execute if score #particle_check sd_temp matches 0 run particle minecraft:flame ~ ~1 ~ 0.2 0.3 0.2 0.05 3 force

# 减少计时器
scoreboard players remove @s sd_fury_timer 1

# 更新持续时间bossbar
execute store result bossbar stardew:dragon_fury_duration value run scoreboard players get @s sd_fury_timer

# 狂怒结束 - 切换到冷却bossbar
execute if score @s sd_fury_timer matches ..0 run bossbar set stardew:dragon_fury_duration visible false
execute if score @s sd_fury_timer matches ..0 store result bossbar stardew:dragon_fury_cooldown value run scoreboard players get @s sd_skill_2_cooldown
execute if score @s sd_fury_timer matches ..0 run bossbar set stardew:dragon_fury_cooldown visible true
execute if score @s sd_fury_timer matches ..0 run function stardew:combat/weapon/dragon_fury_end

