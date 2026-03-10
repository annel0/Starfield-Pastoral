# stardew:weeds/raycast_area_weeds.mcfunction
# 射线循环 - 寻找批量生成杂草的中心位置
# 执行者: 玩家 (@s)
# 执行位置: 射线当前位置

# --- 1. 命中检测 ---
# 检测到非空气方块 -> 在该位置周围5格内批量生成杂草
execute unless block ~ ~ ~ #minecraft:air run function stardew:weeds/spawn_area_weeds
execute unless block ~ ~ ~ #minecraft:air run return 1

# --- 2. 步进 ---
scoreboard players add @s sd_ray_steps 1

# 最多检测 50 步（10格距离，每步0.2格）- 增长射线距离
execute if score @s sd_ray_steps matches ..50 positioned ^ ^ ^0.2 run function stardew:weeds/raycast_area_weeds

# --- 3. 超出范围提示 ---
execute if score @s sd_ray_steps matches 51.. run tellraw @s {"text":"❌ 射线检测范围内无可放置杂草的位置","color":"red"}