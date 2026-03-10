# data/stardew/function/debug/give_tapper_items.mcfunction
# 给予树液提取器和相关物品用于测试

# 给予树液提取器
loot give @s loot stardew:items/utility/tapper

# 给予4种树液产物
loot give @s loot stardew:items/resource/oak_resin
loot give @s loot stardew:items/resource/maple_syrup
loot give @s loot stardew:items/resource/pine_tar
loot give @s loot stardew:items/resource/sap

# 给予镐子用于拆除
loot give @s loot stardew:items/tools/pickaxe_copper

tellraw @s [{"text":"[DEBUG] ","color":"green","bold":true},{"text":"已给予树液提取器、产物和镐子","color":"white"}]
