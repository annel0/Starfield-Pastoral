# data/stardew/function/menu/storage/save_bag_macro.mcfunction
# 保存背包数据的宏
# 宏参数: $(bag_id)

$data modify storage stardew:storage bags[$(bag_id)].items set from entity @s Items
