# ===================================================
# 给予玩家所有 FISH/TRASH 物品
# 使用方法: /function stardew:debug/give_fish_trash
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 fish/trash 物品...","color":"white"}]

loot give @s loot stardew:items/fish/trash/cola_base
loot give @s loot stardew:items/fish/trash/driftwood_base
loot give @s loot stardew:items/fish/trash/garbage_base
loot give @s loot stardew:items/fish/trash/glasses_base
loot give @s loot stardew:items/fish/trash/newspaper_base
loot give @s loot stardew:items/fish/trash/plant_base

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 6 个物品！","color":"green"}]