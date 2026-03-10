# data/stardew/function/utility/chest/drop_single_item.mcfunction
# 递归函数：从列表中取出一个物品并召唤
# 执行者: marker 实体 (@s)

# 1. 取出第一个物品的数据
data modify storage stardew:temp current_item set from entity @s data.Items[0]

# 2. 移除 Slot 字段（容器物品有 Slot，但物品实体不需要）
data remove storage stardew:temp current_item.Slot

# 3. 召唤物品实体
execute at @s run summon item ~ ~ ~ {Item:{id:"minecraft:stone",count:1},PickupDelay:10,Tags:["sd_dropped_item"]}

# 4. 将物品数据复制到实体
execute as @e[type=item,tag=sd_dropped_item,limit=1,sort=nearest] run data modify entity @s Item set from storage stardew:temp current_item

# 5. 清除标记
tag @e[type=item,tag=sd_dropped_item] remove sd_dropped_item

# 6. 从列表中删除已处理的物品
data remove entity @s data.Items[0]

# 7. 如果还有物品，继续递归
execute if data entity @s data.Items[0] run function stardew:utility/chest/drop_single_item
execute if data entity @s data.Items[0] run function stardew:utility/chest/drop_single_item
