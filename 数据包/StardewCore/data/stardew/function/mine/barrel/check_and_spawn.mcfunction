# stardew:mine/barrel/check_and_spawn.mcfunction
# 检查指定位置是否为 stripped_oak_log，并尝试生成木桶
# 参数: $(x), $(z)
# 执行者: 玩家 (@s)

# 检查位置
# Y=65 必须是空气
# Y=64 必须是stripped_oak_log
# 如果满足条件且通过 60% 概率检测，生成木桶
$execute in stardew:mine positioned $(x) 65 $(z) if block ~ ~ ~ air if block ~ ~-1 ~ stripped_oak_log if predicate stardew:random_60 run function stardew:mine/barrel/spawn
