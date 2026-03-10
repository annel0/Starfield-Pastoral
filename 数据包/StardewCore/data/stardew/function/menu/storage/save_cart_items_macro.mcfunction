# data/stardew/function/menu/storage/save_cart_items_macro.mcfunction
# 将临时存储的物品保存到背包数据中
# 参数: $(bag_id) - 背包ID

$data modify storage stardew:storage bags[$(bag_id)].items set from storage stardew:temp cart_items
