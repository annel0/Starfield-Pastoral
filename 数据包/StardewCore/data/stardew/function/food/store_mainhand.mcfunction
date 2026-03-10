# 存储主手食物数据（在消费前）

# 直接存储到临时 storage，使用简单的覆盖方式
data modify storage stardew:temp pending_food set from entity @s SelectedItem.components."minecraft:custom_data"

# 标记玩家ID（简单方法：直接用玩家实体存储）
execute store result storage stardew:temp pending_food.stored_by int 1 run data get entity @s UUID[0]
