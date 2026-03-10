# 让护盾球环绕玩家旋转

# 根据护盾球总数和编号设置不同的角度偏移
scoreboard players set #shield_total sd_temp 0
execute as @e[tag=sd_shield_orb] if score @s sd_shield_id = @p[tag=sd_has_shield] sd_shield_id run scoreboard players add #shield_total sd_temp 1

# 计算角度（3个球120度间隔，4个球90度间隔）
execute if score #shield_total sd_temp matches ..3 if entity @s[tag=sd_orb_1] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches ..3 if entity @s[tag=sd_orb_2] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches ..3 if entity @s[tag=sd_orb_2] run scoreboard players add #angle sd_temp 120
execute if score #shield_total sd_temp matches ..3 if entity @s[tag=sd_orb_3] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches ..3 if entity @s[tag=sd_orb_3] run scoreboard players add #angle sd_temp 240

execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_1] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_2] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_2] run scoreboard players add #angle sd_temp 90
execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_3] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_3] run scoreboard players add #angle sd_temp 180
execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_4] run scoreboard players operation #angle sd_temp = @p[tag=sd_has_shield] sd_shield_rotation
execute if score #shield_total sd_temp matches 4.. if entity @s[tag=sd_orb_4] run scoreboard players add #angle sd_temp 270

execute if score #angle sd_temp matches 360.. run scoreboard players remove #angle sd_temp 360

# 传送到环绕位置
execute store result storage minecraft:stardew temp.angle int 1 run scoreboard players get #angle sd_temp
function stardew:combat/weapon/astral_aegis_teleport with storage minecraft:stardew temp

# 让护盾球始终面向玩家中心（而不是自旋）
execute at @s facing entity @p[tag=sd_has_shield] eyes run tp @s ~ ~ ~ ~ ~

# 粒子效果
particle minecraft:end_rod ~ ~ ~ 0.1 0.1 0.1 0.02 2 force
particle minecraft:enchant ~ ~ ~ 0.15 0.15 0.15 0.3 3 force
execute if entity @s[tag=sd_orb_4] run particle minecraft:soul_fire_flame ~ ~ ~ 0.08 0.08 0.08 0.01 1 force
