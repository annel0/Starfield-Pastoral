# 按钮首次悬停 - 触发高光
# [执行者: item_display按钮]

# 杀死射线
kill @e[tag=sd_shop_ray]

# 标记按钮为被悬停
scoreboard players set @s sd_shop_hover 1

# 如果是首次悬停（上一tick没悬停），触发高光效果
execute unless score @s sd_shop_hover_prev matches 1 run data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.6f,0.6f,0.6f]},Glowing:1b}
