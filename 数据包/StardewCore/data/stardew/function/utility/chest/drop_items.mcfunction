# data/stardew/function/utility/chest/drop_items.mcfunction
# 从 marker 实体的 data.Items 中读取并掉落所有物品
# 执行者: marker 实体 (@s)

# 新方法：递归召唤物品实体，完全不使用临时箱子和 loot mine
# 这样就绝对不会有箱子掉落的问题

# 检查是否还有物品需要掉落
execute at @s if data entity @s data.Items[0] run function stardew:utility/chest/drop_single_item
