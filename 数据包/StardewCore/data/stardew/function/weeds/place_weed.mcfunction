# 放置杂草（使用杂草放置器时调用）
# Shift+右键: 在目标位置生成单个杂草
# 右键: 在目标位置周围5格内批量生成杂草
# 执行者: 玩家 (@s)

# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 检测是否潜行
execute if predicate stardew:is_sneaking at @s anchored eyes positioned ^ ^ ^ run function stardew:weeds/raycast_single_weed
execute unless predicate stardew:is_sneaking at @s anchored eyes positioned ^ ^ ^ run function stardew:weeds/raycast_area_weeds
