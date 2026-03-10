# ================================================================
# 星露谷物语 - 动物向建筑移动
# ================================================================
# 用途：混合寻路方案C - 20格内推动，20格外传送
# 调用：从process_going_home调用，作为动物执行

# 找到所属建筑
scoreboard players operation #find_building stardew.building.temp = @s stardew.animal.building_id
execute as @e[type=marker,tag=stardew.building] if score @s stardew.building.id = #find_building stardew.building.temp run tag @s add stardew.temp.home

# 检查距离
# 如果距离 < 3格 → 到家了，停止
execute if entity @e[type=marker,tag=stardew.temp.home,distance=..3] run tag @s remove stardew.animal.going_home
execute if entity @e[type=marker,tag=stardew.temp.home,distance=..3] run scoreboard players set @s stardew.animal.going_home 0
execute if entity @e[type=marker,tag=stardew.temp.home,distance=..3] run tag @s remove stardew.animal.is_outside
execute if entity @e[type=marker,tag=stardew.temp.home,distance=..3] run scoreboard players set @s stardew.animal.is_outside 0
execute if entity @e[type=marker,tag=stardew.temp.home,distance=..3] run tag @e remove stardew.temp.home
execute unless entity @e[type=marker,tag=stardew.temp.home] run return 0

# 如果距离 > 20格 → 直接传送5格
execute if entity @e[type=marker,tag=stardew.temp.home,distance=20.1..] facing entity @e[type=marker,tag=stardew.temp.home,limit=1] feet run tp @s ^ ^ ^5
execute if entity @e[type=marker,tag=stardew.temp.home,distance=20.1..] at @s run particle cloud ~ ~0.5 ~ 0.3 0.3 0.3 0.01 10
execute if entity @e[type=marker,tag=stardew.temp.home,distance=20.1..] run tag @e remove stardew.temp.home
execute unless entity @e[type=marker,tag=stardew.temp.home] run return 0

# 如果距离 3-20格 → 推动向前移动
execute facing entity @e[type=marker,tag=stardew.temp.home,limit=1] feet run tp @s ^ ^ ^0.3
execute at @s run particle happy_villager ~ ~0.5 ~ 0.2 0.2 0.2 0 2

# 清除标签
tag @e remove stardew.temp.home
