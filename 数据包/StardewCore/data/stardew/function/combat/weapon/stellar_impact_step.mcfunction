# 星辰冲击每一步的粒子和伤害

# 华丽的能量波粒子
particle minecraft:end_rod ~ ~ ~ 0.3 0.3 0.3 0.1 5 force
particle minecraft:soul_fire_flame ~ ~ ~ 0.2 0.2 0.2 0.02 3 force
particle minecraft:glow ~ ~ ~ 0.2 0.2 0.2 0 2 force
particle minecraft:witch ~ ~ ~ 0.1 0.1 0.1 0 1 force

# 检测并标记范围内敌人（只标记，不造成伤害）
execute as @e[type=!minecraft:player,type=!minecraft:item,type=!minecraft:armor_stand,distance=..1.5,tag=!sd_stellar_damaged] run tag @s add sd_stellar_damaged
