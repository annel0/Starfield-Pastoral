# stardew:museum/give_identified
# 给予玩家已鉴定的物品
# macro参数: item_type, item_name

# 根据类型调用对应的loot table
$execute if data storage stardew:temp {item_type:"gem"} run loot give @s loot stardew:items/gems/$(item_name)
$execute if data storage stardew:temp {item_type:"artifact"} run loot give @s loot stardew:items/artifacts/$(item_name)
