# 初始化射线 (通用版)
scoreboard players set @s sd_ray_steps 0
# 保存手持种子的CMD，用于后续清除
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
# 启动循环
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:farming/raycast_loop