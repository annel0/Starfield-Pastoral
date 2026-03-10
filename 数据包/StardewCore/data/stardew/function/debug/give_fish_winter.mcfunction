# ===================================================
# 召唤所有冬季鱼类(所有品质)
# 使用方法: /function stardew:debug/give_fish_winter
# ===================================================

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"召唤冬季鱼类...","color":"white"}]

# 鲈鱼 (Perch) - 44000
loot spawn ~ ~ ~ loot stardew:items/fish/perch_base
loot spawn ~ ~ ~ loot stardew:items/fish/perch_silver
loot spawn ~ ~ ~ loot stardew:items/fish/perch_gold
loot spawn ~ ~ ~ loot stardew:items/fish/perch_diamond

# 乌贼 (Squid) - 44010
loot spawn ~ ~ ~ loot stardew:items/fish/squid_base
loot spawn ~ ~ ~ loot stardew:items/fish/squid_silver
loot spawn ~ ~ ~ loot stardew:items/fish/squid_gold
loot spawn ~ ~ ~ loot stardew:items/fish/squid_diamond

# 长鳍金枪鱼 (Albacore) - 44020
loot spawn ~ ~ ~ loot stardew:items/fish/albacore_base
loot spawn ~ ~ ~ loot stardew:items/fish/albacore_silver
loot spawn ~ ~ ~ loot stardew:items/fish/albacore_gold
loot spawn ~ ~ ~ loot stardew:items/fish/albacore_diamond

# 蛇齿单线鱼 (Lingcod) - 44030
loot spawn ~ ~ ~ loot stardew:items/fish/lingcod_base
loot spawn ~ ~ ~ loot stardew:items/fish/lingcod_silver
loot spawn ~ ~ ~ loot stardew:items/fish/lingcod_gold
loot spawn ~ ~ ~ loot stardew:items/fish/lingcod_diamond

# 传说冰川鱼 (Glacierfish) - 44990
loot spawn ~ ~ ~ loot stardew:items/fish/legend_glacier_base
loot spawn ~ ~ ~ loot stardew:items/fish/legend_glacier_silver
loot spawn ~ ~ ~ loot stardew:items/fish/legend_glacier_gold
loot spawn ~ ~ ~ loot stardew:items/fish/legend_glacier_diamond

tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"已召唤 20 个冬季鱼类物品!","color":"green"}]