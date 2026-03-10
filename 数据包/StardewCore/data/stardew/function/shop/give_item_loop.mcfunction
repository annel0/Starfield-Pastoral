# 循环给予物品
# 根据purchase_count给予对应数量的物品

# 给予一个物品
function stardew:shop/give_item_by_id

# 减少计数
scoreboard players remove #purchase_count sd_temp 1

# 如果还有剩余,继续循环
execute if score #purchase_count sd_temp matches 1.. run function stardew:shop/give_item_loop
