# data/stardew/function/mining/highlight/raycast_loop.mcfunction
# 射线检测循环 - 每步检测是否碰到矿石
# 执行位置: 当前射线位置

# 1. 检测是否碰到矿石 item_display (距离放宽到1格，更容易检测)
execute as @e[type=item_display,tag=sd_stone_display,distance=..1] at @s run function stardew:mining/highlight/apply_highlight

# 2. 检测是否碰到方块 (停止射线)
execute unless block ~ ~ ~ #minecraft:air run return 0

# 3. 增加距离计数
scoreboard players add #raycast_distance sd_temp 1

# 4. 检测距离 (最多检测20步，每步0.25格 = 5格总距离)
execute if score #raycast_distance sd_temp matches ..20 positioned ^ ^ ^0.25 run function stardew:mining/highlight/raycast_loop
