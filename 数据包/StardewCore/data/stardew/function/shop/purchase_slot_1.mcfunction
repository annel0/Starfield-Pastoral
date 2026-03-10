# 购买商品槽位1的物品

# 获取当前槽位的商品数据
data modify storage stardew:temp purchase_item set from storage stardew:temp current_page[0]

# 设置购买数量 (潜行=5, 正常=1)
execute as @a[scores={sd_in_shop=1..},predicate=stardew:is_sneaking] run data modify storage stardew:temp purchase_count set value 5
execute as @a[scores={sd_in_shop=1..},predicate=!stardew:is_sneaking] run data modify storage stardew:temp purchase_count set value 1

# 执行购买
execute as @a[scores={sd_in_shop=1..}] run function stardew:shop/purchase

# 重置interaction数据
data remove entity @s interaction
data remove entity @s attack