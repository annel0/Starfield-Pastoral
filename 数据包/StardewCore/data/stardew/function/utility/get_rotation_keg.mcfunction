# data/stardew/function/utility/get_rotation_keg.mcfunction
# 根据玩家朝向获取Y轴旋转值（四个方向）- 用于小桶
# 执行者: 玩家
# 输出: #rotation sd_temp
# 规则: 东西方向顺时针90度, 南北方向逆时针90度, 然后全部再旋转180度

# 获取玩家的Rotation[0] (y轴旋转，范围-180到180)
execute store result score #yaw sd_temp run data get entity @s Rotation[0] 1

# south: -45 到 45 度 → 逆时针90度+180度 = 90度
execute if score #yaw sd_temp matches -45..45 run scoreboard players set #rotation sd_temp 90

# west: 45 到 135 度 → 顺时针90度+180度 = 0度
execute if score #yaw sd_temp matches 45..135 run scoreboard players set #rotation sd_temp 0

# north: 135 到 180 或 -180 到 -135 度 → 逆时针90度+180度 = 270度
execute if score #yaw sd_temp matches 135.. run scoreboard players set #rotation sd_temp 270
execute if score #yaw sd_temp matches ..-135 run scoreboard players set #rotation sd_temp 270

# east: -135 到 -45 度 → 顺时针90度+180度 = 180度
execute if score #yaw sd_temp matches -135..-45 run scoreboard players set #rotation sd_temp 180
