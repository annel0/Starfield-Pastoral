# stardew:monsters/drops/dust_sprite_loot.mcfunction
# 尘埃精灵掉落物

# 基础掉落 - 煤炭 (50%)
execute if predicate stardew:random_50 run loot spawn ~ ~ ~ loot minecraft:blocks/coal_ore

# 稀有掉落 - 矮人卷轴 (0.5%)
execute if predicate stardew:random_0_5 run loot spawn ~ ~ ~ loot stardew:items/monster_loot/dwarf_scroll_1

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 7

# 标记已处理
tag @s add sd_loot_processed
