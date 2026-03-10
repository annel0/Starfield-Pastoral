# 持续毒性伤害处理
# 每秒造成毒性伤害

# 减少毒性计时器
scoreboard players remove @s sd_poison_timer 1

# 每20 ticks（1秒）造成一次伤害
scoreboard players operation #check_tick sd_temp = @s sd_poison_timer
scoreboard players operation #check_tick sd_temp %= #20 sd_const

execute if score #check_tick sd_temp matches 0 run function stardew:combat/weapon/poison_tick_damage

# 粒子效果（每5 ticks一次）
scoreboard players operation #check_particle sd_temp = @s sd_poison_timer
scoreboard players set #5 sd_const 5
scoreboard players operation #check_particle sd_temp %= #5 sd_const
execute if score #check_particle sd_temp matches 0 run particle minecraft:item_slime ~ ~1 ~ 0.2 0.4 0.2 0.05 3 force

# 毒性结束
execute if score @s sd_poison_timer matches ..0 run function stardew:combat/weapon/poison_end
