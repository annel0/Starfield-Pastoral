# data/stardew/functions/tools/hoe/main_router.mcfunction
# 初始化锄头射线
scoreboard players set @s sd_ray_steps 0
# [核心] 必须使用 anchored eyes positioned ^ ^ ^
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:tools/hoe/raycast_loop