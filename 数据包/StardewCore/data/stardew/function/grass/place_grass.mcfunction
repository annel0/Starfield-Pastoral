# 放置草（使用草放置器时调用）
# Shift+右键: 在目标位置生成单个草
# 右键: 在目标位置周围5格内批量生成草（整片生成）
# 执行者: 玩家 (@s)

# Debug信息


# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 检测是否潜行
execute if predicate stardew:is_sneaking at @s anchored eyes positioned ^ ^ ^ run function stardew:grass/raycast_single_grass
execute unless predicate stardew:is_sneaking at @s anchored eyes positioned ^ ^ ^ run function stardew:grass/raycast_area_grass