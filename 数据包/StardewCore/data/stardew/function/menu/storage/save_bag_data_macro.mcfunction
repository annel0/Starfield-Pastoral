# data/stardew/function/menu/storage/save_bag_data_macro.mcfunction
# 使用宏保存背包数据到storage
# 参数: $(bag_id)

# 从矿车实体保存Items到storage
$data modify storage stardew:storage bags[$(bag_id)].items set from entity @s Items
