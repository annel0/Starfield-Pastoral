# data/stardew/functions/tools/charge_fov.mcfunction
# 蓄力时的视觉效果（粒子反馈）- 减少粒子数量

# Minecraft无法直接修改FOV，所以使用粒子效果代替

# 根据蓄力时间显示不同的粒子效果（降低频率，每5 tick显示一次）
scoreboard players operation #charge_particle sd_temp = @s sd_charge_time
scoreboard players operation #charge_particle sd_temp %= #5 sd_const

# 只在 charge_time % 5 == 0 时显示粒子
execute if score #charge_particle sd_temp matches 0 if score @s sd_charge_time matches 1..10 at @s run particle minecraft:dust{color:[1.0,1.0,0.0],scale:0.5} ~ ~1.5 ~ 0.2 0.2 0.2 0 1 force @s
execute if score #charge_particle sd_temp matches 0 if score @s sd_charge_time matches 11..20 at @s run particle minecraft:dust{color:[1.0,0.5,0.0],scale:0.7} ~ ~1.5 ~ 0.25 0.25 0.25 0 1 force @s
execute if score #charge_particle sd_temp matches 0 if score @s sd_charge_time matches 21..30 at @s run particle minecraft:dust{color:[1.0,0.0,0.0],scale:0.9} ~ ~1.5 ~ 0.3 0.3 0.3 0 1 force @s
execute if score #charge_particle sd_temp matches 0 if score @s sd_charge_time matches 31..40 at @s run particle minecraft:dust{color:[0.8,0.0,1.0],scale:1.1} ~ ~1.5 ~ 0.35 0.35 0.35 0 1 force @s
