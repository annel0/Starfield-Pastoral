# ===================================================
# 召唤所有秋季鱼类(所有品质)
# 使用方法: /function stardew:debug/give_fish_fall
# ===================================================

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"召唤秋季鱼类...","color":"white"}]

# 鲑鱼 (Salmon) - 43000
loot spawn ~ ~ ~ loot stardew:items/fish/salmon_base
loot spawn ~ ~ ~ loot stardew:items/fish/salmon_silver
loot spawn ~ ~ ~ loot stardew:items/fish/salmon_gold
loot spawn ~ ~ ~ loot stardew:items/fish/salmon_diamond

# 虎纹鳟鱼 (Tiger Trout) - 43010
loot spawn ~ ~ ~ loot stardew:items/fish/tiger_trout_base
loot spawn ~ ~ ~ loot stardew:items/fish/tiger_trout_silver
loot spawn ~ ~ ~ loot stardew:items/fish/tiger_trout_gold
loot spawn ~ ~ ~ loot stardew:items/fish/tiger_trout_diamond

# 大口黑鲈 (Largemouth Bass) - 43020
loot spawn ~ ~ ~ loot stardew:items/fish/largemouth_bass_base
loot spawn ~ ~ ~ loot stardew:items/fish/largemouth_bass_silver
loot spawn ~ ~ ~ loot stardew:items/fish/largemouth_bass_gold
loot spawn ~ ~ ~ loot stardew:items/fish/largemouth_bass_diamond

# 红鲷鱼 (Red Snapper) - 43030
loot spawn ~ ~ ~ loot stardew:items/fish/red_snapper_base
loot spawn ~ ~ ~ loot stardew:items/fish/red_snapper_silver
loot spawn ~ ~ ~ loot stardew:items/fish/red_snapper_gold
loot spawn ~ ~ ~ loot stardew:items/fish/red_snapper_diamond

# 海参 (Sea Cucumber) - 43040
loot spawn ~ ~ ~ loot stardew:items/fish/sea_cucumber_base
loot spawn ~ ~ ~ loot stardew:items/fish/sea_cucumber_silver
loot spawn ~ ~ ~ loot stardew:items/fish/sea_cucumber_gold
loot spawn ~ ~ ~ loot stardew:items/fish/sea_cucumber_diamond

# 大眼鲈鱼 (Walleye) - 43050
loot spawn ~ ~ ~ loot stardew:items/fish/walleye_base
loot spawn ~ ~ ~ loot stardew:items/fish/walleye_silver
loot spawn ~ ~ ~ loot stardew:items/fish/walleye_gold
loot spawn ~ ~ ~ loot stardew:items/fish/walleye_diamond

# 午夜鲤鱼 (Midnight Carp) - 43060
loot spawn ~ ~ ~ loot stardew:items/fish/midnight_carp_base
loot spawn ~ ~ ~ loot stardew:items/fish/midnight_carp_silver
loot spawn ~ ~ ~ loot stardew:items/fish/midnight_carp_gold
loot spawn ~ ~ ~ loot stardew:items/fish/midnight_carp_diamond

# 海鳗 (Sea Eel) - 43070
loot spawn ~ ~ ~ loot stardew:items/fish/sea_eel_base
loot spawn ~ ~ ~ loot stardew:items/fish/sea_eel_silver
loot spawn ~ ~ ~ loot stardew:items/fish/sea_eel_gold
loot spawn ~ ~ ~ loot stardew:items/fish/sea_eel_diamond

# 传说琵琶鱼 (Angler) - 43990
loot spawn ~ ~ ~ loot stardew:items/fish/legend_angler_base
loot spawn ~ ~ ~ loot stardew:items/fish/legend_angler_silver
loot spawn ~ ~ ~ loot stardew:items/fish/legend_angler_gold
loot spawn ~ ~ ~ loot stardew:items/fish/legend_angler_diamond

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"已召唤 36 个秋季鱼类物品!","color":"green"}]