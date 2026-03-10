# data/stardew/functions/farming/water_action.mcfunction
# [执行位置: 耕地内部]

# 1. 获取水壶等级 (CMD)
# 虽然不能改 NBT，但读取 CMD 是完全没问题的
execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 2. 范围分发
# 铜级 (301) -> T1 (单格)
execute if score @s sd_const matches 301 run function stardew:tools/watering_can/t1_water
# 铁级 (302) -> T2 (3x3)
execute if score @s sd_const matches 302 run function stardew:tools/watering_can/t2_water
# 金级 (303) -> T3 (5x5)
execute if score @s sd_const matches 303 run function stardew:tools/watering_can/t3_water
# 钻石级 (304) -> T4 (5x10)
execute if score @s sd_const matches 304 run function stardew:tools/watering_can/t4_water