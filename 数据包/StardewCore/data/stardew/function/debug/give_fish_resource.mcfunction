# ===================================================
# 给予玩家所有 FISH/RESOURCE 物品
# 使用方法: /function stardew:debug/give_fish_resource
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 fish/resource 物品...","color":"white"}]

loot give @s loot stardew:items/fish/resource/green_algae_base
loot give @s loot stardew:items/fish/resource/seaweed_base
loot give @s loot stardew:items/fish/resource/white_algae_base

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 3 个物品！","color":"green"}]