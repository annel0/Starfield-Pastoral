# 将下方的土壤转换为草方块
# 在生成草之前调用，确保下方是草方块
# 执行位置：草要生成的位置（地面上方）

execute if block ~ ~-1 ~ minecraft:dirt run setblock ~ ~-1 ~ minecraft:grass_block
execute if block ~ ~-1 ~ minecraft:coarse_dirt run setblock ~ ~-1 ~ minecraft:grass_block
execute if block ~ ~-1 ~ minecraft:podzol run setblock ~ ~-1 ~ minecraft:grass_block