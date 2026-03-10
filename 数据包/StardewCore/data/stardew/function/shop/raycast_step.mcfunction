# 商店射线步进 - 递归检测
# [执行者: marker]

# 碰到方块就停止
execute unless block ~ ~ ~ #stardew:air_like run kill @s

# 距离过远就停止
execute if entity @a[tag=shop_raycast_player,distance=6..] run kill @s

# 检测附近的item_display按钮 (0.35格范围)
execute as @e[type=item_display,tag=shop_ui,tag=shop_button,distance=..0.35] run function stardew:shop/on_hover

# 前进0.3格
tp @s ^ ^ ^0.3

# 递归继续
execute if entity @s at @s run function stardew:shop/raycast_step