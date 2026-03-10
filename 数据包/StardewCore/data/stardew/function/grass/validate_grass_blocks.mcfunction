# 验证所有草下方是否为草方块，如果不是则转换
# 每天调用，确保草方块逻辑的一致性

execute as @e[type=interaction,tag=sd_grass] at @s run function stardew:grass/convert_to_grass_block