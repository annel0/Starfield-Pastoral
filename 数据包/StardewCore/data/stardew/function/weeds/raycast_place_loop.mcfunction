# stardew:weeds/raycast_place_loop.mcfunction
# 射线循环 - 寻找可放置杂草的位置
# 执行者: 玩家 (@s)
# 执行位置: 射线当前位置

# DEBUG粒子（可选）
# particle minecraft:dust{color:[0.0,1.0,0.0],scale:0.3} ~ ~ ~ 0 0 0 0 1 force @a[distance=..20]

# --- 1. 命中检测 ---
# 检测到非空气方块 -> 尝试在方块上方生成杂草
execute unless block ~ ~ ~ #minecraft:air run function stardew:weeds/try_spawn_at_location
execute unless block ~ ~ ~ #minecraft:air run return 1

# --- 2. 步进 ---
scoreboard players add @s sd_ray_steps 1

# 最多检测 30 步（6格距离，每步0.2格）
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:weeds/raycast_place_loop

# --- 3. 超出范围提示 ---
execute if score @s sd_ray_steps matches 31.. run tellraw @s {"text":"❌ 未找到可放置的位置（距离太远）","color":"red"}
