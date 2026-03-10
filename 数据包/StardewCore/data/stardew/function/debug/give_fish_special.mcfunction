# ===================================================
# 给予玩家所有 FISH/SPECIAL 物品
# 使用方法: /function stardew:debug/give_fish_special
# ===================================================

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"给予 fish/special 物品...","color":"white"}]

loot give @s loot stardew:items/fish/special/ghostfish_base
loot give @s loot stardew:items/fish/special/ghostfish_diamond
loot give @s loot stardew:items/fish/special/ghostfish_gold
loot give @s loot stardew:items/fish/special/ghostfish_silver
loot give @s loot stardew:items/fish/special/ice_pip_base
loot give @s loot stardew:items/fish/special/ice_pip_diamond
loot give @s loot stardew:items/fish/special/ice_pip_gold
loot give @s loot stardew:items/fish/special/ice_pip_silver
loot give @s loot stardew:items/fish/special/lava_eel_base
loot give @s loot stardew:items/fish/special/lava_eel_diamond
loot give @s loot stardew:items/fish/special/lava_eel_gold
loot give @s loot stardew:items/fish/special/lava_eel_silver
loot give @s loot stardew:items/fish/special/sandfish_base
loot give @s loot stardew:items/fish/special/sandfish_diamond
loot give @s loot stardew:items/fish/special/sandfish_gold
loot give @s loot stardew:items/fish/special/sandfish_silver
loot give @s loot stardew:items/fish/special/scorpion_carp_base
loot give @s loot stardew:items/fish/special/scorpion_carp_diamond
loot give @s loot stardew:items/fish/special/scorpion_carp_gold
loot give @s loot stardew:items/fish/special/scorpion_carp_silver
loot give @s loot stardew:items/fish/special/stonefish_base
loot give @s loot stardew:items/fish/special/stonefish_diamond
loot give @s loot stardew:items/fish/special/stonefish_gold
loot give @s loot stardew:items/fish/special/stonefish_silver

tellraw @s [{"text":"[星露谷] ","color":"gold"},{"text":"已给予 24 个物品！","color":"green"}]