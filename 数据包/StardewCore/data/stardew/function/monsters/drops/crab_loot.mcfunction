# stardew:monsters/drops/crab_loot.mcfunction
# 蟹类掉落物

# 基础掉落 - 石头 (30%)
execute if predicate stardew:random_30 run loot spawn ~ ~ ~ loot minecraft:blocks/stone

# 稀有掉落 - 矮人卷轴 (0.5%)
execute if predicate stardew:random_0_5 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_2

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 10

# 标记已处理
tag @s add sd_loot_processed
