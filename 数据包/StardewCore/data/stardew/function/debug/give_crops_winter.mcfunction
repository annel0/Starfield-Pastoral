# ===================================================
# 给予玩家所有 CROPS/WINTER 物品
# 使用方法: /function stardew:debug/give_crops_winter
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 crops/winter 物品...","color":"white"}]

loot give @s loot stardew:items/crops/winter/ancient_fruit_base
loot give @s loot stardew:items/crops/winter/ancient_fruit_diamond
loot give @s loot stardew:items/crops/winter/ancient_fruit_gold
loot give @s loot stardew:items/crops/winter/ancient_fruit_silver
loot give @s loot stardew:items/crops/winter/crystal_fruit_base
loot give @s loot stardew:items/crops/winter/crystal_fruit_diamond
loot give @s loot stardew:items/crops/winter/crystal_fruit_gold
loot give @s loot stardew:items/crops/winter/crystal_fruit_silver
loot give @s loot stardew:items/crops/winter/snow_yam_base
loot give @s loot stardew:items/crops/winter/snow_yam_diamond
loot give @s loot stardew:items/crops/winter/snow_yam_gold
loot give @s loot stardew:items/crops/winter/snow_yam_silver
loot give @s loot stardew:items/crops/winter/winter_root_base
loot give @s loot stardew:items/crops/winter/winter_root_diamond
loot give @s loot stardew:items/crops/winter/winter_root_gold
loot give @s loot stardew:items/crops/winter/winter_root_silver
loot give @s loot stardew:items/crops/winter/powder_melon_base
loot give @s loot stardew:items/crops/winter/powder_melon_diamond
loot give @s loot stardew:items/crops/winter/powder_melon_gold
loot give @s loot stardew:items/crops/winter/powder_melon_silver

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 20 个物品！","color":"green"}]