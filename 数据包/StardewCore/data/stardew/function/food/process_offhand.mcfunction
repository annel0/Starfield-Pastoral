# 处理副手食物的恢复效果
# 读取 custom_data 中的恢复值

# 临时存储物品数据
data modify storage stardew:temp food_data set from entity @s Inventory[{Slot:-106b}].components."minecraft:custom_data"

# 检查是否真的是可食用物品
execute unless data storage stardew:temp food_data.is_food run return 0

# 恢复生命和能量
function stardew:food/restore with storage stardew:temp food_data

# 清理临时数据
data remove storage stardew:temp food_data
