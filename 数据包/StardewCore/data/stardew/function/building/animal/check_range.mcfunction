# ================================================================
# 星露谷物语 - 检查动物范围
# ================================================================
# 用途：检查单个动物是否在建筑允许范围内
# 调用：从manage_range调用，作为动物执行

# 临时存储动物的建筑ID
scoreboard players operation #check_building_id stardew.building.temp = @s stardew.animal.building_id

# 标记对应的建筑
execute as @e[type=marker,tag=stardew.building] if score @s stardew.building.id = #check_building_id stardew.building.temp run tag @s add stardew.temp.target_building

# 检查距离并应用限制
execute at @s as @e[type=marker,tag=stardew.temp.target_building,limit=1] at @s run function stardew:building/animal/apply_range_limit

# 清除临时标签
tag @e[tag=stardew.temp.target_building] remove stardew.temp.target_building
