# stardew:monsters/drops/ghost_loot.mcfunction
# 幽灵掉落物

# 基础掉落 - 虫肉 (50%)
execute if predicate stardew:random_50 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/bug_meat

# 稀有掉落 - 太阳精华 (20%)
execute if predicate stardew:random_20 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/solar_essence

# 超稀有 - 矮人卷轴 IV (0.8%)
execute if predicate stardew:random_1 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_4

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 12

# 标记已处理
tag @s add sd_loot_processed
