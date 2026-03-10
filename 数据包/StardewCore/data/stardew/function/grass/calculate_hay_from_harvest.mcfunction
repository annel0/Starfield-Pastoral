# 镰刀收割后计算干草
# 执行者: 玩家 (@s)



# 检查是否有收割的草
execute unless score @s sd_grass_harvested matches 1.. run return 0

# 初始化筒仓数据
function stardew:grass/init_silo_data

# 检查玩家是否有筒仓

execute unless score @s sd_silo_count matches 1.. run return run scoreboard players set @s sd_grass_harvested 0

# 检查筒仓是否已满
execute if score @s sd_hay_stored >= @s sd_hay_capacity run return run scoreboard players set @s sd_grass_harvested 0

# 获取镰刀类型
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"



# 设置基础概率（根据镰刀类型）
execute if score @s sd_temp matches 101 run scoreboard players set @s sd_hay_chance 25
execute if score @s sd_temp matches 102 run scoreboard players set @s sd_hay_chance 50
execute if score @s sd_temp matches 103 run scoreboard players set @s sd_hay_chance 75
execute if score @s sd_temp matches 104 run scoreboard players set @s sd_hay_chance 100



# 冬天概率下降67%（即乘以33%）
execute if score Global sd_season matches 4 run scoreboard players operation @s sd_hay_chance *= #33 sd_const
execute if score Global sd_season matches 4 run scoreboard players operation @s sd_hay_chance /= #100 sd_const

# 为每片草单独判断概率
scoreboard players set @s sd_temp 0
scoreboard players set grass_counter sd_temp 1

# 按期望值计算干草，但有保底机制
scoreboard players set @s sd_temp 0

# 计算期望干草数 = 收割数量 * 概率 / 100
scoreboard players operation @s sd_temp = @s sd_grass_harvested
scoreboard players operation @s sd_temp *= @s sd_hay_chance
scoreboard players operation @s sd_temp /= #100 sd_const



# 保底机制：收割10片以上草且期望为0时，给1个干草
execute if score @s sd_grass_harvested matches 10.. if score @s sd_temp matches 0 run scoreboard players set @s sd_temp 1


# 检查筒仓容量限制
scoreboard players operation @s sd_random = @s sd_hay_capacity
scoreboard players operation @s sd_random -= @s sd_hay_stored
execute if score @s sd_temp > @s sd_random run scoreboard players operation @s sd_temp = @s sd_random

# 添加干草到筒仓
scoreboard players operation @s sd_hay_stored += @s sd_temp

# 显示结果
execute if score @s sd_temp matches 1.. run tellraw @s [{"text":"收割了","color":"green"},{"score":{"name":"@s","objective":"sd_grass_harvested"},"color":"yellow"},{"text":"片草，获得了","color":"green"},{"score":{"name":"@s","objective":"sd_temp"},"color":"yellow"},{"text":"个干草。筒仓: ","color":"green"},{"score":{"name":"@s","objective":"sd_hay_stored"},"color":"yellow"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"yellow"}]
execute unless score @s sd_temp matches 1.. run tellraw @s [{"text":"收割了","color":"green"},{"score":{"name":"@s","objective":"sd_grass_harvested"},"color":"yellow"},{"text":"片草，但没有获得干草。","color":"gray"}]

# 不在这里重置计数器，让镰刀函数处理