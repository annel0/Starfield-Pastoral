# 检测玩家食用物品
# 在 main.mcfunction 中每 tick 调用

# 重置使用计分板
execute as @a[scores={sd_use_item=1..}] run function stardew:food/try_consume
