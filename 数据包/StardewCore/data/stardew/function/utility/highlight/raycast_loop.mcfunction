# data/stardew/function/utility/highlight/raycast_loop.mcfunction
# 射线检测循环 - 每步检测是否碰到实用设施
# 执行位置: 当前射线位置

# 1. 检测是否碰到实用设施的视觉实体 (item_display)
execute as @e[type=item_display,tag=sd_utility,distance=..0.6] at @s run function stardew:utility/highlight/apply_highlight

# 2. 检测是否碰到方块 (停止射线)
execute unless block ~ ~ ~ #minecraft:air run return 0

# 3. 检测距离 (最多5格)
execute if entity @a[tag=sd_utility_raycast_player,distance=..5] positioned ^ ^ ^0.3 run function stardew:utility/highlight/raycast_loop
