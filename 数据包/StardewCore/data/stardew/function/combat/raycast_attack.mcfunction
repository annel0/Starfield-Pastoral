               # 射线检测攻击（递归）

# 检测是否命中怪物（如果命中，在当前位置标记怪物并调用hit_monster）
execute if entity @e[tag=sd_monster,distance=..2] run tag @e[tag=sd_monster,distance=..2,limit=1,sort=nearest] add sd_hit_target
execute if entity @e[tag=sd_monster,distance=..2] positioned as @p run function stardew:combat/hit_monster
execute if entity @e[tag=sd_monster,distance=..2] run return 0

# 检测是否超出武器范围（使用武器的weapon_range属性）
execute if score #raycast_distance sd_temp >= #weapon_range sd_temp run return 0

# 继续射线
scoreboard players add #raycast_distance sd_temp 1
execute positioned ^ ^ ^0.3 run function stardew:combat/raycast_attack
