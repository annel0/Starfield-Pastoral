# stardew:monsters/drops/golem_loot.mcfunction
# 傀儡掉落物

# 基础掉落 - 石头 (50%)
execute if predicate stardew:random_50 run loot spawn ~ ~ ~ loot minecraft:blocks/stone
execute if predicate stardew:random_50 run loot spawn ~ ~ ~ loot minecraft:blocks/stone

# 稀有掉落 - 石英 (10%)
execute if predicate stardew:random_10 run loot spawn ~ ~ ~ loot minecraft:blocks/quartz_block

# 给予经验
execute as @p[distance=..10] run scoreboard players add @s sd_combat_xp 20

# 标记已处理
tag @s add sd_loot_processed
