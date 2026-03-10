# data/stardew/function/menu/hover/raycast_start.mcfunction
# [执行者: 玩家] 开始射线检测

# 给玩家tag
tag @s add sd_raycast_player

# 先清理所有旧marker
kill @e[tag=sd_menu_ray]

# 射线 - 用临时tag确保选中正确的
summon marker ~ ~ ~ {Tags:["sd_menu_ray_new"]}
execute positioned ~ ~1.7 ~ run tp @e[tag=sd_menu_ray_new] ~ ~ ~ ~ ~

# 改名
tag @e[tag=sd_menu_ray_new] add sd_menu_ray
tag @e[tag=sd_menu_ray_new] remove sd_menu_ray_new

# 执行射线循环
execute as @e[tag=sd_menu_ray] at @s run function stardew:menu/hover/raycast_loop

# 去除tag
tag @s remove sd_raycast_player
