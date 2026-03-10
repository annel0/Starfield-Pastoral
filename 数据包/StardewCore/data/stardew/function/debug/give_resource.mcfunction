# ===================================================
# 给予玩家所有 RESOURCE 物品
# 使用方法: /function stardew:debug/give_resource
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 resource 物品...","color":"white"}]

loot give @s loot stardew:items/resource/hardwood
loot give @s loot stardew:items/resource/wood

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 2 个物品！","color":"green"}]