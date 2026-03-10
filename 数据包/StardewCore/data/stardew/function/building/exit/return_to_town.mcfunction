# 从室内返回镇上
# TODO: 需要记录玩家进入时的坐标，这里暂时传送到镇中心

# 根据不同建筑返回不同位置（需要后续根据实际建筑位置调整）
execute if entity @s[tag=inside_pierre_shop] run function stardew:building/exit/to_pierre_shop
execute if entity @s[tag=inside_saloon] run function stardew:building/exit/to_saloon
execute if entity @s[tag=inside_clinic] run function stardew:building/exit/to_clinic
execute if entity @s[tag=inside_blacksmith] run function stardew:building/exit/to_blacksmith
execute if entity @s[tag=inside_museum] run function stardew:building/exit/to_museum
execute if entity @s[tag=inside_community_center] run function stardew:building/exit/to_community_center
execute if entity @s[tag=inside_joja_mart] run function stardew:building/exit/to_joja_mart

execute if entity @s[tag=inside_1_river_road] run function stardew:building/exit/to_1_river_road
execute if entity @s[tag=inside_1_willow_lane] run function stardew:building/exit/to_1_willow_lane
execute if entity @s[tag=inside_2_willow_lane] run function stardew:building/exit/to_2_willow_lane
execute if entity @s[tag=inside_trailer] run function stardew:building/exit/to_trailer
execute if entity @s[tag=inside_mayor_manor] run function stardew:building/exit/to_mayor_manor

# 清除所有室内标签
tag @s remove inside_building
tag @s remove inside_pierre_shop
tag @s remove inside_saloon
tag @s remove inside_clinic
tag @s remove inside_blacksmith
tag @s remove inside_museum
tag @s remove inside_community_center
tag @s remove inside_joja_mart
tag @s remove inside_house
tag @s remove inside_1_river_road
tag @s remove inside_1_willow_lane
tag @s remove inside_2_willow_lane
tag @s remove inside_trailer
tag @s remove inside_mayor_manor

# 播放音效和粒子
playsound minecraft:block.wooden_door.close master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"离开建筑","color":"yellow"}
