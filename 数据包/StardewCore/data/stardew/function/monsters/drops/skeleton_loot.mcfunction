# stardew:monsters/drops/skeleton_loot.mcfunction
# 骷髅掉落物

# 基础掉落 - 骨头 (90%)
execute if predicate stardew:random_90 run loot spawn ~ ~ ~ loot minecraft:entities/skeleton

# 稀有掉落 - 太阳精华 (15%)
execute if predicate stardew:random_15 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/solar_essence

# 超稀有 - 矮人卷轴 (1%)
execute if predicate stardew:random_1 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_3

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 15

# 标记已处理
tag @s add sd_loot_processed
