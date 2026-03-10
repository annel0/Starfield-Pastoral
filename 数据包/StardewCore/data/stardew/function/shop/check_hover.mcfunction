# 商店悬停检测 - 使用marker射线检测
# [执行者: 玩家] 检测视线瞄准的按钮

# 给玩家tag
tag @s add shop_raycast_player

# 清理旧marker
kill @e[tag=shop_ray]

# 召唤射线marker
summon marker ~ ~1.7 ~ {Tags:["shop_ray"]}
tp @e[tag=shop_ray,limit=1] ~ ~1.7 ~ ~ ~

# 执行射线循环
execute as @e[tag=shop_ray] at @s run function stardew:shop/raycast_step

# 去除tag
tag @s remove shop_raycast_player