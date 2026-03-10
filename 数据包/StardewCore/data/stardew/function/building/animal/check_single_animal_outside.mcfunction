# ================================================================
# 星露谷物语 - 检查单个动物是否被困在外
# ================================================================
# 用途：检查动物是否在建筑10格外（被困）
# 调用：从check_animals_outside调用，作为动物执行

# 找到所属建筑
scoreboard players operation #check_building stardew.building.temp = @s stardew.animal.building_id
execute as @e[type=marker,tag=stardew.building] if score @s stardew.building.id = #check_building stardew.building.temp run tag @s add stardew.temp.check_building

# 检查距离和门状态
execute at @s as @e[type=marker,tag=stardew.temp.check_building,limit=1] at @s run function stardew:building/animal/apply_outside_penalty

# 清除标签
tag @e remove stardew.temp.check_building
