# data/stardew/function/tools/debug_spawn_maple.mcfunction
# DEBUG工具：生成枫树 (CMD: 2511)

# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 从玩家眼睛位置开始射线检测，生成成熟枫树
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:tools/debug_tree_maple_raycast