# 处理持续回血效果

# 每20 ticks（1秒）恢复一次
scoreboard players operation #regen_check sd_temp = @s sd_regen_timer
scoreboard players operation #regen_check sd_temp %= #20 sd_const

# 如果是20的倍数，恢复生命值
execute if score #regen_check sd_temp matches 0 run scoreboard players operation @s sd_health += @s sd_regen_amount
execute if score #regen_check sd_temp matches 0 if score @s sd_health > @s sd_max_health run scoreboard players operation @s sd_health = @s sd_max_health

# 粒子效果（每5 tick一次）
scoreboard players operation #particle_check sd_temp = @s sd_regen_timer
scoreboard players set #5 sd_const 5
scoreboard players operation #particle_check sd_temp %= #5 sd_const
execute if score #particle_check sd_temp matches 0 run particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0 2 force

# 减少计时器
scoreboard players remove @s sd_regen_timer 1

# 更新持续时间bossbar
execute store result bossbar stardew:forest_blessing_duration value run scoreboard players get @s sd_regen_timer

# 回血结束 - 切换到冷却bossbar
execute if score @s sd_regen_timer matches ..0 run bossbar set stardew:forest_blessing_duration visible false
execute if score @s sd_regen_timer matches ..0 store result bossbar stardew:forest_blessing_cooldown value run scoreboard players get @s sd_skill_cooldown
execute if score @s sd_regen_timer matches ..0 run bossbar set stardew:forest_blessing_cooldown visible true

# 回血结束提示（已删除actionbar占用）
execute if score @s sd_regen_timer matches 0 run playsound minecraft:block.grass.break player @s ~ ~ ~ 0.5 0.5
