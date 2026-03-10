# stardew:monsters/drops/bug_loot.mcfunction
# 昆虫掉落物

# 基础掉落 - 虫肉 (80%)
execute if predicate stardew:random_80 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/bug_meat

# 稀有掉落 - 矮人卷轴 II (0.5%)
execute if predicate stardew:random_0_5 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_2

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 4

# 标记已处理
tag @s add sd_loot_processed
