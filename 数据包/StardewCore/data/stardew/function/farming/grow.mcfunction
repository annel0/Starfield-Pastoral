# data/stardew/functions/farming/grow.mcfunction

# 1. 年龄增加
scoreboard players add @s sd_crop_age 1
# 锁定最大年龄 (不超过 sd_max_crop_age)
execute if score @s sd_crop_age > @s sd_max_crop_age run scoreboard players operation @s sd_crop_age = @s sd_max_crop_age

# 2. 刷新贴图 (Mapping Age 0-4 -> Block Age 0-7)
execute if score @s sd_crop_age matches 0 run setblock ~ ~ ~ minecraft:wheat[age=0]
execute if score @s sd_crop_age matches 1 run setblock ~ ~ ~ minecraft:wheat[age=2]
execute if score @s sd_crop_age matches 2 run setblock ~ ~ ~ minecraft:wheat[age=4]
execute if score @s sd_crop_age matches 3 run setblock ~ ~ ~ minecraft:wheat[age=6]
execute if score @s sd_crop_age matches 4.. run setblock ~ ~ ~ minecraft:wheat[age=7]

# 3. 成熟特效 (达到max_age时才触发)
execute if score @s sd_crop_age >= @s sd_max_crop_age run particle minecraft:happy_villager ~ ~0.5 ~ 0.2 0.2 0.2 0 5
