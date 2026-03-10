# data/stardew/function/utility/get_rotation.mcfunction
# 根据玩家朝向获取Y轴旋转值（四个方向）- 用于熔炉
# 执行者: 玩家
# 输出: #rotation sd_temp (0=south, 90=west, 180=north, 270=east)

# 获取玩家的Rotation[0] (y轴旋转，范围-180到180)
execute store result score #yaw sd_temp run data get entity @s Rotation[0] 1

# south: -45 到 45 度 → 180度旋转 (修正: 南北对调)
execute if score #yaw sd_temp matches -45..45 run scoreboard players set #rotation sd_temp 180

# west: 45 到 135 度 → 90度旋转  
execute if score #yaw sd_temp matches 45..135 run scoreboard players set #rotation sd_temp 90

# north: 135 到 180 或 -180 到 -135 度 → 0度旋转 (修正: 南北对调)
execute if score #yaw sd_temp matches 135.. run scoreboard players set #rotation sd_temp 0
execute if score #yaw sd_temp matches ..-135 run scoreboard players set #rotation sd_temp 0

# east: -135 到 -45 度 → 270度旋转
execute if score #yaw sd_temp matches -135..-45 run scoreboard players set #rotation sd_temp 270
