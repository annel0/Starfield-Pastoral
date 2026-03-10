# 护盾球飞行追踪

# 减少计时器
scoreboard players remove @s sd_temp 1

# 粒子轨迹
particle minecraft:end_rod ~ ~ ~ 0.15 0.15 0.15 0.05 5 force
particle minecraft:enchant ~ ~ ~ 0.2 0.2 0.2 0.3 4 force
particle minecraft:soul_fire_flame ~ ~ ~ 0.1 0.1 0.1 0.02 2 force

# 让item_display旋转得更快（飞行时）
data modify entity @s transformation.left_rotation set value [0f,1f,0f,0f]

# 朝向最近的怪物并移动
execute facing entity @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] eyes positioned ^ ^ ^0.8 run tp @s ~ ~ ~ ~ ~

# 检测是否击中怪物
execute positioned ~-0.8 ~-0.8 ~-0.8 if entity @e[tag=sd_monster,dx=1.5,dy=1.5,dz=1.5] run function stardew:combat/weapon/astral_aegis_hit_target

# 时间到或击中后消失
execute if score @s sd_temp matches ..0 run function stardew:combat/weapon/astral_aegis_orb_vanish
