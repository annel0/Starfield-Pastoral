# stardew:mine/barrel/try_spawn.mcfunction
# 尝试在当前位置生成木桶
# 执行位置: stripped_oak_log位置
# 执行者: 玩家 (@s)

# 每个stripped_oak_log都有60%概率生成木桶
execute if predicate stardew:random_60 run function stardew:mine/barrel/spawn
