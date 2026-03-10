# ===================================================
# 给予玩家所有春季鱼类(所有品质)
# 使用方法: /function stardew:debug/give_fish_spring
# ===================================================

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"召唤春季鱼类...","color":"white"}]

# 小嘴鲈鱼 (Smallmouth Bass) - 41000
loot spawn ~ ~ ~ loot stardew:items/fish/smallmouth_bass_base
loot spawn ~ ~ ~ loot stardew:items/fish/smallmouth_bass_silver
loot spawn ~ ~ ~ loot stardew:items/fish/smallmouth_bass_gold
loot spawn ~ ~ ~ loot stardew:items/fish/smallmouth_bass_diamond

# 西鲱 (Shad) - 41010
loot spawn ~ ~ ~ loot stardew:items/fish/shad_base
loot spawn ~ ~ ~ loot stardew:items/fish/shad_silver
loot spawn ~ ~ ~ loot stardew:items/fish/shad_gold
loot spawn ~ ~ ~ loot stardew:items/fish/shad_diamond

# 鲶鱼 (Catfish) - 41020
loot spawn ~ ~ ~ loot stardew:items/fish/catfish_base
loot spawn ~ ~ ~ loot stardew:items/fish/catfish_silver
loot spawn ~ ~ ~ loot stardew:items/fish/catfish_gold
loot spawn ~ ~ ~ loot stardew:items/fish/catfish_diamond

# 太阳鱼 (Sunfish) - 41030
loot spawn ~ ~ ~ loot stardew:items/fish/sunfish_base
loot spawn ~ ~ ~ loot stardew:items/fish/sunfish_silver
loot spawn ~ ~ ~ loot stardew:items/fish/sunfish_gold
loot spawn ~ ~ ~ loot stardew:items/fish/sunfish_diamond

# 鳀鱼 (Anchovy) - 41040
loot spawn ~ ~ ~ loot stardew:items/fish/anchovy_base
loot spawn ~ ~ ~ loot stardew:items/fish/anchovy_silver
loot spawn ~ ~ ~ loot stardew:items/fish/anchovy_gold
loot spawn ~ ~ ~ loot stardew:items/fish/anchovy_diamond

# 沙丁鱼 (Sardine) - 41050
loot spawn ~ ~ ~ loot stardew:items/fish/sardine_base
loot spawn ~ ~ ~ loot stardew:items/fish/sardine_silver
loot spawn ~ ~ ~ loot stardew:items/fish/sardine_gold
loot spawn ~ ~ ~ loot stardew:items/fish/sardine_diamond

# 大头鱼 (Bullhead) - 41060
loot spawn ~ ~ ~ loot stardew:items/fish/bullhead_base
loot spawn ~ ~ ~ loot stardew:items/fish/bullhead_silver
loot spawn ~ ~ ~ loot stardew:items/fish/bullhead_gold
loot spawn ~ ~ ~ loot stardew:items/fish/bullhead_diamond

# 大比目鱼 (Halibut) - 41080
loot spawn ~ ~ ~ loot stardew:items/fish/halibut_base
loot spawn ~ ~ ~ loot stardew:items/fish/halibut_silver
loot spawn ~ ~ ~ loot stardew:items/fish/halibut_gold
loot spawn ~ ~ ~ loot stardew:items/fish/halibut_diamond

# 鳗鱼 (Eel) - 41090
loot spawn ~ ~ ~ loot stardew:items/fish/eel_base
loot spawn ~ ~ ~ loot stardew:items/fish/eel_silver
loot spawn ~ ~ ~ loot stardew:items/fish/eel_gold
loot spawn ~ ~ ~ loot stardew:items/fish/eel_diamond

# 传说深红鱼 (Legend Crimson) - 41990
loot spawn ~ ~ ~ loot stardew:items/fish/legend_crimson_base
loot spawn ~ ~ ~ loot stardew:items/fish/legend_crimson_silver
loot spawn ~ ~ ~ loot stardew:items/fish/legend_crimson_gold
loot spawn ~ ~ ~ loot stardew:items/fish/legend_crimson_diamond

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"已召唤 40 个春季鱼类物品！","color":"green"}]