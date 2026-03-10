# data/stardew/function/menu/storage/update_cart_position.mcfunction
# 每tick更新矿车位置，让它跟随玩家
# 执行者: 玩家（在tick_cart中通过 as @a at @s 调用）

# tp矿车到玩家前方，水平放置，再顺时针转90度
execute at @s rotated ~ 0 positioned ^ ^ ^1.5 store result score #FacingYaw sd_storage_temp run data get entity @s Rotation[0]
execute at @s rotated ~ 0 positioned ^ ^ ^1.5 run scoreboard players add #FacingYaw sd_storage_temp 90
execute at @s rotated ~ 0 positioned ^ ^ ^1.5 store result storage stardew:temp cart_yaw float 1 run scoreboard players get #FacingYaw sd_storage_temp
execute at @s rotated ~ 0 positioned ^ ^ ^1.5 run function stardew:menu/storage/tp_cart_with_rotation with storage stardew:temp
