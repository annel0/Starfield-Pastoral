# data/stardew/function/utility/chest/place_raycast.mcfunction
# 箱子放置射线检测初始化

# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 添加标记
tag @s add sd_placing_chest

# 启动射线循环
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:utility/chest/place_raycast_loop
