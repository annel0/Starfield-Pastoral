# ===================================================
# 召唤所有夏季鱼类(所有品质)
# 使用方法: /function stardew:debug/give_fish_summer
# ===================================================

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"召唤夏季鱼类...","color":"white"}]

# 虹鳟鱼 (Rainbow Trout) - 42000
loot spawn ~ ~ ~ loot stardew:items/fish/rainbow_trout_base
loot spawn ~ ~ ~ loot stardew:items/fish/rainbow_trout_silver
loot spawn ~ ~ ~ loot stardew:items/fish/rainbow_trout_gold
loot spawn ~ ~ ~ loot stardew:items/fish/rainbow_trout_diamond

# 罗非鱼 (Tilapia) - 42010
loot spawn ~ ~ ~ loot stardew:items/fish/tilapia_base
loot spawn ~ ~ ~ loot stardew:items/fish/tilapia_silver
loot spawn ~ ~ ~ loot stardew:items/fish/tilapia_gold
loot spawn ~ ~ ~ loot stardew:items/fish/tilapia_diamond

# 红鲻鱼 (Red Mullet) - 42020
loot spawn ~ ~ ~ loot stardew:items/fish/red_mullet_base
loot spawn ~ ~ ~ loot stardew:items/fish/red_mullet_silver
loot spawn ~ ~ ~ loot stardew:items/fish/red_mullet_gold
loot spawn ~ ~ ~ loot stardew:items/fish/red_mullet_diamond

# 狗鱼 (Pike) - 42030
loot spawn ~ ~ ~ loot stardew:items/fish/pike_base
loot spawn ~ ~ ~ loot stardew:items/fish/pike_silver
loot spawn ~ ~ ~ loot stardew:items/fish/pike_gold
loot spawn ~ ~ ~ loot stardew:items/fish/pike_diamond

# 金枪鱼 (Tuna) - 42040
loot spawn ~ ~ ~ loot stardew:items/fish/tuna_base
loot spawn ~ ~ ~ loot stardew:items/fish/tuna_silver
loot spawn ~ ~ ~ loot stardew:items/fish/tuna_gold
loot spawn ~ ~ ~ loot stardew:items/fish/tuna_diamond

# 鲟鱼 (Sturgeon) - 42050
loot spawn ~ ~ ~ loot stardew:items/fish/sturgeon_base
loot spawn ~ ~ ~ loot stardew:items/fish/sturgeon_silver
loot spawn ~ ~ ~ loot stardew:items/fish/sturgeon_gold
loot spawn ~ ~ ~ loot stardew:items/fish/sturgeon_diamond

# 河豚 (Pufferfish) - 42060
loot spawn ~ ~ ~ loot stardew:items/fish/pufferfish_base
loot spawn ~ ~ ~ loot stardew:items/fish/pufferfish_silver
loot spawn ~ ~ ~ loot stardew:items/fish/pufferfish_gold
loot spawn ~ ~ ~ loot stardew:items/fish/pufferfish_diamond

# 章鱼 (Octopus) - 42070
loot spawn ~ ~ ~ loot stardew:items/fish/octopus_base
loot spawn ~ ~ ~ loot stardew:items/fish/octopus_silver
loot spawn ~ ~ ~ loot stardew:items/fish/octopus_gold
loot spawn ~ ~ ~ loot stardew:items/fish/octopus_diamond

# 超级海参 (Super Cucumber) - 42080
loot spawn ~ ~ ~ loot stardew:items/fish/super_cucumber_base
loot spawn ~ ~ ~ loot stardew:items/fish/super_cucumber_silver
loot spawn ~ ~ ~ loot stardew:items/fish/super_cucumber_gold
loot spawn ~ ~ ~ loot stardew:items/fish/super_cucumber_diamond

# 剑旗鱼 (Dorado) - 42090
loot spawn ~ ~ ~ loot stardew:items/fish/dorado_base
loot spawn ~ ~ ~ loot stardew:items/fish/dorado_silver
loot spawn ~ ~ ~ loot stardew:items/fish/dorado_gold
loot spawn ~ ~ ~ loot stardew:items/fish/dorado_diamond

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"已召唤 40 个夏季鱼类物品!","color":"green"}]