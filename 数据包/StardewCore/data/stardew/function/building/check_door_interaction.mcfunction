# 检查玩家附近的门交互体并进入建筑
# 执行者：点击门的玩家 (@s)

# 商业建筑
execute if entity @e[type=interaction,tag=door_pierre_shop,distance=..3] run function stardew:building/enter/pierre_shop
execute if entity @e[type=interaction,tag=door_saloon,distance=..3] run function stardew:building/enter/saloon
execute if entity @e[type=interaction,tag=door_clinic,distance=..3] run function stardew:building/enter/clinic
execute if entity @e[type=interaction,tag=door_blacksmith,distance=..3] run function stardew:building/enter/blacksmith
execute if entity @e[type=interaction,tag=door_museum,distance=..3] run function stardew:building/enter/museum
execute if entity @e[type=interaction,tag=door_community_center,distance=..3] run function stardew:building/enter/community_center
execute if entity @e[type=interaction,tag=door_joja_mart,distance=..3] run function stardew:building/enter/joja_mart

# 居民住宅
execute if entity @e[type=interaction,tag=door_1_river_road,distance=..3] run function stardew:building/enter/house_1_river_road
execute if entity @e[type=interaction,tag=door_1_willow_lane,distance=..3] run function stardew:building/enter/house_1_willow_lane
execute if entity @e[type=interaction,tag=door_2_willow_lane,distance=..3] run function stardew:building/enter/house_2_willow_lane
execute if entity @e[type=interaction,tag=door_trailer,distance=..3] run function stardew:building/enter/trailer
execute if entity @e[type=interaction,tag=door_mayor_manor,distance=..3] run function stardew:building/enter/mayor_manor
