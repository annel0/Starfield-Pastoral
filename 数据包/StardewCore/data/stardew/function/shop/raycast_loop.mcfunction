# 商店射线检测循环
# [执行者: 射线marker]

# Debug粒子 (可选)
# particle end_rod ~ ~ ~ 0 0 0 0 1 force @a

# 碰到方块时停止
execute unless block ~ ~ ~ #stardew:air_like run kill @s

# 过远时停止 (6格外)
execute if entity @a[tag=sd_shop_raycast,distance=6..] run kill @s

# 检测是否击中interaction实体（槽位和按钮）
execute as @e[type=interaction,tag=shop_interaction,tag=slot_1,distance=..0.7] at @s run function stardew:shop/hover_slot_1
execute as @e[type=interaction,tag=shop_interaction,tag=slot_2,distance=..0.7] at @s run function stardew:shop/hover_slot_2
execute as @e[type=interaction,tag=shop_interaction,tag=slot_3,distance=..0.7] at @s run function stardew:shop/hover_slot_3

# 检测按钮（直接检测item_display按钮）
execute as @e[type=item_display,tag=shop_button,distance=..0.7] run function stardew:shop/on_hover_button

# 前进0.3格
tp @s ^ ^ ^0.3

# 如果还存在，继续循环
execute if entity @s at @s run function stardew:shop/raycast_loop
