# 商店射线检测 - 开始
# [执行者: 在商店中的玩家]

# 给玩家tag
tag @s add sd_shop_raycast

# 清理旧射线marker
kill @e[tag=sd_shop_ray]

# 生成射线marker (从眼睛位置开始)
summon marker ~ ~ ~ {Tags:["sd_shop_ray_new"]}
execute positioned ~ ~1.62 ~ run tp @e[tag=sd_shop_ray_new] ~ ~ ~ ~ ~

# 改名
tag @e[tag=sd_shop_ray_new] add sd_shop_ray
tag @e[tag=sd_shop_ray_new] remove sd_shop_ray_new

# 执行射线循环
execute as @e[tag=sd_shop_ray] at @s run function stardew:shop/raycast_loop

# 去除tag
tag @s remove sd_shop_raycast
