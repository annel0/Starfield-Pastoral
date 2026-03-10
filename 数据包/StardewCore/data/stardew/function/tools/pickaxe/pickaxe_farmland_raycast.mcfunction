# data/stardew/function/tools/pickaxe/pickaxe_farmland_raycast.mcfunction
# 镐子射线检测耕地 - 入口

# 清除标记
tag @s remove sd_hit_farmland

# 初始化射线步数
scoreboard players set @s sd_ray_steps 0

# 启动循环
execute anchored eyes positioned ^ ^ ^ run function stardew:tools/pickaxe/pickaxe_farmland_raycast_loop
