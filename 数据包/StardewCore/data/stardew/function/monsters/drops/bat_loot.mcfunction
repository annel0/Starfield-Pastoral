# stardew:monsters/drops/bat_loot.mcfunction
# 蝙蝠掉落物

# 基础掉落 - 蝙蝠翅膀 (70%)
execute if predicate stardew:random_70 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/bat_wing

# 稀有掉落 - 矮人卷轴 III (0.5%)
execute if predicate stardew:random_0_5 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_3

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 8

# 标记已处理
tag @s add sd_loot_processed
