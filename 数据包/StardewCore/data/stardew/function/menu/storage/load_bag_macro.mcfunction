# data/stardew/function/menu/storage/load_bag_macro.mcfunction
# 使用宏加载背包数据
# 宏参数: $(bag_id)

# 从storage加载Items到矿车实体
$data modify entity @s Items set from storage stardew:storage bags[$(bag_id)].items
