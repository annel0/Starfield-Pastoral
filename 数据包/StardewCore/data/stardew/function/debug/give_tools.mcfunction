# ===================================================
# 给予玩家所有 TOOLS 物品
# 使用方法: /function stardew:debug/give_tools
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 tools 物品...","color":"white"}]

loot give @s loot stardew:items/tools/axe_copper
loot give @s loot stardew:items/tools/axe_diamond
loot give @s loot stardew:items/tools/axe_gold
loot give @s loot stardew:items/tools/axe_iron
loot give @s loot stardew:items/tools/hoe_copper
loot give @s loot stardew:items/tools/hoe_diamond
loot give @s loot stardew:items/tools/hoe_gold
loot give @s loot stardew:items/tools/hoe_iron
loot give @s loot stardew:items/tools/scythe_copper
loot give @s loot stardew:items/tools/scythe_diamond
loot give @s loot stardew:items/tools/scythe_gold
loot give @s loot stardew:items/tools/scythe_iron
loot give @s loot stardew:items/tools/watering_can_copper
loot give @s loot stardew:items/tools/watering_can_diamond
loot give @s loot stardew:items/tools/watering_can_gold
loot give @s loot stardew:items/tools/watering_can_iron

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 16 个物品！","color":"green"}]