# 持续灼烧伤害处理
# 每秒造成灼烧伤害

# 减少灼烧计时器
scoreboard players remove @s sd_burning_timer 1

# 每20 ticks（1秒）造成一次伤害
scoreboard players operation #check_tick sd_temp = @s sd_burning_timer
scoreboard players operation #check_tick sd_temp %= #20 sd_const

execute if score #check_tick sd_temp matches 0 run function stardew:combat/weapon/burning_tick_damage

# 粒子效果（每5 ticks一次）
scoreboard players operation #check_particle sd_temp = @s sd_burning_timer
scoreboard players set #5 sd_const 5
scoreboard players operation #check_particle sd_temp %= #5 sd_const
execute if score #check_particle sd_temp matches 0 run particle minecraft:flame ~ ~0.5 ~ 0.2 0.4 0.2 0.02 5 force
execute if score #check_particle sd_temp matches 0 run particle minecraft:smoke ~ ~0.5 ~ 0.1 0.3 0.1 0.01 2 force

# 灼烧结束
execute if score @s sd_burning_timer matches ..0 run function stardew:combat/weapon/burning_end
