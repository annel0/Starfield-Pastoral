# 连击技能的射线检测（只标记，不造成伤害）

# 检查是否超出范围
execute if score #ray_distance sd_temp >= #weapon_range sd_temp run return 0

# 增加距离
scoreboard players add #ray_distance sd_temp 1

# 检测是否击中实体（标记怪物）
execute positioned ~-0.3 ~-0.3 ~-0.3 as @e[tag=sd_monster,dx=0,dy=0,dz=0,tag=!sd_rapid_strike_target,limit=1] run tag @s add sd_rapid_strike_target

# 如果击中目标或碰到方块，停止
execute if entity @e[tag=sd_rapid_strike_target,distance=..1] run return 0
execute unless block ~ ~ ~ #minecraft:air run return 0

# 继续向前
execute positioned ^ ^ ^0.1 run function stardew:combat/weapon/rapid_strike_raycast
