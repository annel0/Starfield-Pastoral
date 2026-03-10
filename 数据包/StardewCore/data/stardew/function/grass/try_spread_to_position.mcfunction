# 尝试在指定位置生成新草
# 在扩散时调用
# 执行位置: 目标位置

# 检查地面是否适合（土壤类型）
execute unless block ~ ~ ~ #minecraft:dirt unless block ~ ~ ~ minecraft:grass_block unless block ~ ~ ~ minecraft:coarse_dirt unless block ~ ~ ~ minecraft:podzol run return 0

# 检查上方是否有空间
execute unless block ~ ~1 ~ #minecraft:air run return 0

# 检查是否已有草或其他实体
execute if entity @e[type=minecraft:interaction,tag=sd_grass,distance=..1] run return 0
execute if entity @e[type=minecraft:interaction,distance=..1] run return 0

# 将下方土壤变为草方块
function stardew:grass/convert_to_grass_block

# 生成新草
execute align xyz positioned ~0.5 ~ ~0.5 run function stardew:grass/spawn_random_grass

# 生成扩散特效
particle minecraft:happy_villager ~ ~0.5 ~ 0.3 0.3 0.3 0 5