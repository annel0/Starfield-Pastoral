# ===================================================
# 给予玩家所有 FISHING 物品
# 使用方法: /function stardew:debug/give_fishing
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 fishing 物品...","color":"white"}]

loot give @s loot stardew:items/fishing/rod_copper
loot give @s loot stardew:items/fishing/rod_diamond
loot give @s loot stardew:items/fishing/rod_gold
loot give @s loot stardew:items/fishing/rod_iron
loot give @s loot stardew:items/fishing/tackle_barbed
loot give @s loot stardew:items/fishing/tackle_cork
loot give @s loot stardew:items/fishing/tackle_quality
loot give @s loot stardew:items/fishing/tackle_sonar
loot give @s loot stardew:items/fishing/tackle_spinner
loot give @s loot stardew:items/fishing/tackle_trap
loot give @s loot stardew:items/fishing/tackle_treasure

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 11 个物品！","color":"green"}]