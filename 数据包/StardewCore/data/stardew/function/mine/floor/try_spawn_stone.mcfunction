# stardew:mine/floor/try_spawn_stone.mcfunction
# 尝试在指定位置生成石头或木桶
# 参数: $(x), $(z) - 绝对坐标
# 执行者: 玩家 (@s)

# 检查位置是否可用:
# 1. Y=65 必须是空气 (地板上方一格，可以放东西)
# 2. Y=64 必须不是空气 (下方有实心地板)
# 因为生成前会统一清空整个区域为空气，所以下方不是空气就说明是结构的地板

# 先检查下方是否为 stripped_oak_log (去皮橡木原木)，如果是则尝试生成木桶
$execute in stardew:mine positioned $(x) 65 $(z) if block ~ ~ ~ air if block ~ ~-1 ~ stripped_oak_log run function stardew:mine/barrel/try_spawn

# 检查下方不是空气且不是木桶地板，生成矿石
$execute in stardew:mine positioned $(x) 65 $(z) if block ~ ~ ~ air unless block ~ ~-1 ~ air unless block ~ ~-1 ~ stripped_oak_log run function stardew:mine/ore/spawn_random_ore

# 注意: 石头计数现在由 spawn_stones_direct.mcfunction 结束时统一计算
