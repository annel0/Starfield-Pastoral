# 尝试在当前位置生成单个草
# 执行者: 玩家 (@s)
# 执行位置: 草要生成的位置（地面上方）

# 检查下方是否是合适的地面（土壤类型）
execute unless block ~ ~-1 ~ #minecraft:dirt unless block ~ ~-1 ~ minecraft:grass_block unless block ~ ~-1 ~ minecraft:coarse_dirt unless block ~ ~-1 ~ minecraft:podzol run return run tellraw @s {"text":"[Debug] 草只能放置在土壤上","color":"red"}

# 检查当前位置是否有足够的空间（应该是空气）
execute unless block ~ ~ ~ #minecraft:air run return run tellraw @s {"text":"此位置空间不足，无法放置草","color":"red"}

# 检查是否已有草
execute if entity @e[type=minecraft:interaction,tag=sd_grass,distance=..1] run return run tellraw @s {"text":"此位置已有草，无法重复放置","color":"yellow"}

# 将下方的土壤变为草方块
function stardew:grass/convert_to_grass_block

# 生成草实体（对齐到方块中心底部）
execute align xyz positioned ~0.5 ~ ~0.5 run function stardew:grass/spawn_random_grass

# 成功提示
tellraw @s {"text":"成功放置了1个草","color":"green"}