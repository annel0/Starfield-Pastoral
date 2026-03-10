# ================================================================
# 星露谷物语 - 应用范围限制
# ================================================================
# 用途：根据门状态和动物位置应用范围限制
# 调用：从check_range调用，作为建筑marker执行，动物在@s位置附近

# 检查门是否关闭
execute unless score @s stardew.building.door_open matches 1 run function stardew:building/animal/door_closed_logic

# 门开着时：根据动物实际位置更新 is_outside 标签
execute if score @s stardew.building.door_open matches 1 as @e[type=#stardew:animals,tag=stardew.animal,distance=..10] if score @s stardew.animal.building_id = @e[type=marker,tag=stardew.building,limit=1,sort=nearest] stardew.building.id run tag @s remove stardew.animal.is_outside
execute if score @s stardew.building.door_open matches 1 as @e[type=#stardew:animals,tag=stardew.animal,distance=..10] if score @s stardew.animal.building_id = @e[type=marker,tag=stardew.building,limit=1,sort=nearest] stardew.building.id run scoreboard players set @s stardew.animal.is_outside 0
execute if score @s stardew.building.door_open matches 1 as @e[type=#stardew:animals,tag=stardew.animal,distance=10.1..] if score @s stardew.animal.building_id = @e[type=marker,tag=stardew.building,limit=1,sort=nearest] stardew.building.id run tag @s add stardew.animal.is_outside
execute if score @s stardew.building.door_open matches 1 as @e[type=#stardew:animals,tag=stardew.animal,distance=10.1..] if score @s stardew.animal.building_id = @e[type=marker,tag=stardew.building,limit=1,sort=nearest] stardew.building.id run scoreboard players set @s stardew.animal.is_outside 1
