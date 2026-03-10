# data/stardew/function/menu/storage/load_bag_data_macro.mcfunction
# 使用宏从storage加载背包数据

# 这里需要根据玩家UUID和背包ID加载数据
# 暂时使用占位符 - 需要配合数据存储系统
$data modify entity @s Items set from storage stardew:storage players[{UUID:$(uuid)}].bags[{id:$(bag_id)}].items
