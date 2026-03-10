# data/stardew/function/tools/debug_spawn_mahogany.mcfunction
# DEBUG工具：生成桃花心木 (CMD: 2513)

# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 从玩家眼睛位置开始射线检测，生成成熟桃花心木
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:tools/debug_tree_mahogany_raycast