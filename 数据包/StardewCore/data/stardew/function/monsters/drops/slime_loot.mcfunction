# stardew:monsters/drops/slime_loot.mcfunction
# 史莱姆掉落物

# 基础掉落 - 史莱姆 (75%)
execute if predicate stardew:random_75 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/slime

# 稀有掉落 - 矮人卷轴 I (0.5%)
execute if predicate stardew:random_0_5 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_1

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 5

# 清除原版掉落
kill @s

# 标记已处理
tag @s add sd_loot_processed
