# ================================================================
# 星露谷物语 - 动物范围限制管理器
# ================================================================
# 用途：每tick检查动物是否超出建筑范围，根据门状态限制活动
# 调用：从主tick调用

# 处理所有有归属建筑的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id matches 1.. run function stardew:building/animal/check_range
