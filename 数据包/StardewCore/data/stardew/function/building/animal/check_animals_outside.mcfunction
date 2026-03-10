# ================================================================
# 星露谷物语 - 检查动物是否在外面（22:00）
# ================================================================
# 用途：22:00检查，如果动物还在外面且门关着，标记为被困在外
# 调用：从time/calc在22:00时调用

# 处理所有有建筑的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id matches 1.. run function stardew:building/animal/check_single_animal_outside
