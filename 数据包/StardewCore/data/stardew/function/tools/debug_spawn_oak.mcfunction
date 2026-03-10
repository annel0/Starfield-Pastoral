# data/stardew/function/tools/debug_spawn_oak.mcfunction
# DEBUG工具：生成橡树 (CMD: 2510)

# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 从玩家眼睛位置开始射线检测，生成成熟橡树
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:tools/debug_tree_oak_raycast

# 反馈信息
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"橡树生成工具已使用","color":"green"}]