# data/stardew/function/debug/give_mining_items.mcfunction
# 给予所有挖矿相关物品

tellraw @s {"text":"=== 挖矿系统物品 ===","color":"gold","bold":true}

# 镐子
tellraw @s {"text":"[镐子]","color":"aqua","bold":true}
loot give @s loot stardew:items/tools/pickaxe_copper
loot give @s loot stardew:items/tools/pickaxe_iron
loot give @s loot stardew:items/tools/pickaxe_gold
loot give @s loot stardew:items/tools/pickaxe_diamond

# 矿石资源
tellraw @s {"text":"[矿石资源]","color":"yellow","bold":true}
loot give @s loot stardew:items/resource/stone
loot give @s loot stardew:items/resource/coal
loot give @s loot stardew:items/resource/copper_ore
loot give @s loot stardew:items/resource/iron_ore
loot give @s loot stardew:items/resource/gold_ore
loot give @s loot stardew:items/resource/diamond

# 宝石
tellraw @s {"text":"[宝石]","color":"light_purple","bold":true}
loot give @s loot stardew:items/gems/quartz
loot give @s loot stardew:items/gems/earth_crystal
loot give @s loot stardew:items/gems/frozen_tear
loot give @s loot stardew:items/gems/jade
loot give @s loot stardew:items/gems/ruby
loot give @s loot stardew:items/gems/amethyst
loot give @s loot stardew:items/gems/prismatic_shard

# 未鉴定宝石
tellraw @s {"text":"[未鉴定宝石]","color":"dark_purple","bold":true}
loot give @s loot stardew:items/gems/quartz_unknown
loot give @s loot stardew:items/gems/earth_crystal_unknown
loot give @s loot stardew:items/gems/frozen_tear_unknown
loot give @s loot stardew:items/gems/jade_unknown
loot give @s loot stardew:items/gems/ruby_unknown
loot give @s loot stardew:items/gems/amethyst_unknown
loot give @s loot stardew:items/gems/prismatic_shard_unknown

tellraw @s {"text":"✓ 已给予所有挖矿物品！","color":"green"}
