# ===================================================
# 给予玩家所有 DEBUG 物品
# 使用方法: /function stardew:debug/give_debug
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 debug 物品...","color":"white"}]

loot give @s loot stardew:items/debug/fish_doctor
loot give @s loot stardew:items/debug/grow_hormone
loot give @s loot stardew:items/debug/time_wand
loot give @s loot stardew:items/debug/weather_wand

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 4 个物品！","color":"green"}]