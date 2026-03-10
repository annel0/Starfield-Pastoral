# 存储副手食物数据（在消费前）

# 直接存储到临时 storage
data modify storage stardew:temp pending_food set from entity @s Inventory[{Slot:-106b}].components."minecraft:custom_data"

# 标记玩家ID
execute store result storage stardew:temp pending_food.stored_by int 1 run data get entity @s UUID[0]
