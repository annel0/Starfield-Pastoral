# 尝试在当前位置生成区域草
# 在草生成区域调用，每个位置都尝试生成（无概率限制）
# 执行者: 玩家 (@s)

# 检查是否在合适的地面上（土壤类型）
execute unless block ~ ~ ~ minecraft:dirt unless block ~ ~ ~ minecraft:grass_block unless block ~ ~ ~ minecraft:coarse_dirt unless block ~ ~ ~ minecraft:podzol run return 0

# 检查上方是否有空间
execute unless block ~ ~1 ~ minecraft:air run return 0

# 检查是否已有草
execute if entity @e[type=minecraft:interaction,tag=sd_grass,distance=..0.5] run return 0

# 将下方的土壤变为草方块
function stardew:grass/convert_to_grass_block

# 生成草实体
function stardew:grass/spawn_random_grass

# 增加计数器
scoreboard players add grass_count_tmp sd_temp 1