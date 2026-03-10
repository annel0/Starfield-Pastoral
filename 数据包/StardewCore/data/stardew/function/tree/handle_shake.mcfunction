# data/stardew/functions/tree/handle_shake.mcfunction

execute unless score @s sd_crop_age matches 14.. run return 0
# 1. 状态检查
execute if score @s sd_shaked matches 1 run playsound minecraft:block.grass.step block @a ~ ~ ~ 0.5 0.5
execute if score @s sd_shaked matches 1 run return 1

# 2. 效果
playsound minecraft:block.grass.place block @a ~ ~ ~ 1 0.8
particle minecraft:block{block_state:"minecraft:oak_leaves"} ~ ~2.5 ~ 0.5 0.5 0.5 1 20

# 3. 掉落种子 (Loot Table)
# 橡果 (1)
execute if score @s sd_tree_type matches 1 run loot spawn ~ ~0.5 ~ loot stardew:items/seeds/tree_oak
# 枫树种子 (2)
execute if score @s sd_tree_type matches 2 run loot spawn ~ ~0.5 ~ loot stardew:items/seeds/tree_maple
# 松果 (3)
execute if score @s sd_tree_type matches 3 run loot spawn ~ ~0.5 ~ loot stardew:items/seeds/tree_pine
# 桃花心木种子 (4)
execute if score @s sd_tree_type matches 4 run loot spawn ~ ~0.5 ~ loot stardew:items/seeds/tree_mahogany

# 4. 置位
scoreboard players set @s sd_shaked 1