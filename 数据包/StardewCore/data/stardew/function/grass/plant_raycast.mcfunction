# 草籽种植的射线检测
# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 启动循环
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:grass/plant_raycast_loop