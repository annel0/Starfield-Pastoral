# 显示护盾球粒子效果（环绕旋转）

# 中心能量核心（更强烈）
particle minecraft:end_rod ~ ~1 ~ 0.3 0.3 0.3 0.05 5 force
particle minecraft:enchant ~ ~1 ~ 0.4 0.4 0.4 1 8 force
execute if score @s sd_shield_count matches 4.. run particle minecraft:soul_fire_flame ~ ~1 ~ 0.25 0.25 0.25 0.02 3 force

# 护盾球1（0度基准）
execute if score @s sd_shield_count matches 1.. store result storage minecraft:stardew temp.angle int 1 run scoreboard players get @s sd_shield_rotation
execute if score @s sd_shield_count matches 1.. run function stardew:combat/weapon/astral_aegis_particle_sphere with storage minecraft:stardew temp

# 护盾球2（120度）
execute if score @s sd_shield_count matches 2.. run scoreboard players operation #angle2 sd_temp = @s sd_shield_rotation
execute if score @s sd_shield_count matches 2.. run scoreboard players add #angle2 sd_temp 120
execute if score @s sd_shield_count matches 2.. if score #angle2 sd_temp matches 360.. run scoreboard players remove #angle2 sd_temp 360
execute if score @s sd_shield_count matches 2.. store result storage minecraft:stardew temp.angle int 1 run scoreboard players get #angle2 sd_temp
execute if score @s sd_shield_count matches 2.. run function stardew:combat/weapon/astral_aegis_particle_sphere with storage minecraft:stardew temp

# 护盾球3（240度）
execute if score @s sd_shield_count matches 3.. run scoreboard players operation #angle3 sd_temp = @s sd_shield_rotation
execute if score @s sd_shield_count matches 3.. run scoreboard players add #angle3 sd_temp 240
execute if score @s sd_shield_count matches 3.. if score #angle3 sd_temp matches 360.. run scoreboard players remove #angle3 sd_temp 360
execute if score @s sd_shield_count matches 3.. store result storage minecraft:stardew temp.angle int 1 run scoreboard players get #angle3 sd_temp
execute if score @s sd_shield_count matches 3.. run function stardew:combat/weapon/astral_aegis_particle_sphere with storage minecraft:stardew temp

# 护盾球4（90度 - 无限之刃专属）
execute if score @s sd_shield_count matches 4.. run scoreboard players operation #angle4 sd_temp = @s sd_shield_rotation
execute if score @s sd_shield_count matches 4.. run scoreboard players add #angle4 sd_temp 90
execute if score @s sd_shield_count matches 4.. if score #angle4 sd_temp matches 360.. run scoreboard players remove #angle4 sd_temp 360
execute if score @s sd_shield_count matches 4.. store result storage minecraft:stardew temp.angle int 1 run scoreboard players get #angle4 sd_temp
execute if score @s sd_shield_count matches 4.. run function stardew:combat/weapon/astral_aegis_particle_sphere with storage minecraft:stardew temp

# 地面能量环（让效果更震撼）
particle minecraft:witch ~ ~0.1 ~ 1.8 0 1.8 0 3 force
particle minecraft:portal ~ ~0.5 ~ 1.5 0.3 1.5 0 2 force
