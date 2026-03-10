# data/stardew/function/utility/highlight/raycast_start.mcfunction
# 开始射线检测 - 检测玩家是否指向实用设施
# 执行者: 玩家 (@s)

# 1. 总是执行高亮检测(不再限制只有拿镐子)
# 这样玩家拿任何物品都能看到设施高亮,方便交互

# 2. 添加射线检测标签
tag @s add sd_utility_raycast_player

# 3. 从玩家眼睛位置开始射线检测
execute anchored eyes positioned ^ ^ ^ run function stardew:utility/highlight/raycast_loop

# 4. 清除标签
tag @s remove sd_utility_raycast_player
