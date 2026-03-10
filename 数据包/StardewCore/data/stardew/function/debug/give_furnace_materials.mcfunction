# data/stardew/function/debug/give_furnace_materials.mcfunction
# 给予熔炉所需材料 (调试用)

# 给予 25 个石头
loot give @s loot stardew:items/resources/stone
loot give @s loot stardew:items/resources/stone
loot give @s loot stardew:items/resources/stone
loot give @s loot stardew:items/resources/stone
loot give @s loot stardew:items/resources/stone

# 给予 20 个铜矿石
loot give @s loot stardew:items/resources/copper_ore
loot give @s loot stardew:items/resources/copper_ore
loot give @s loot stardew:items/resources/copper_ore
loot give @s loot stardew:items/resources/copper_ore

tellraw @s [{"text":"[DEBUG] ","color":"gray"},{"text":"已给予熔炉材料: 25石头 + 20铜矿石","color":"green"}]
