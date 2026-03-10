# stardew:monsters/spawn/spawn_on_floor.mcfunction
# 根据当前层数生成怪物（入口函数）
# 执行者: 玩家 (@s)
# 执行环境: in stardew:mine

# 清理旧怪物（已经在 kill_floor_entities 中处理，这里不需要重复）
# execute in stardew:mine run kill @e[tag=sd_monster]

# 获取当前楼层并保存到 storage
execute store result storage stardew:monsters spawn.floor int 1 run scoreboard players get @s sd_mine_floor

# 设置房间边界（X: 6-24, Z: 从 storage 获取）
# 矿物区域: X:6~24, Z:z+6~z+24
scoreboard players set #room_x_min sd_temp 6
scoreboard players set #room_x_max sd_temp 24
execute store result score #room_z_min sd_temp run data get storage stardew:mine gen.z6
execute store result score #room_z_max sd_temp run data get storage stardew:mine gen.z24

# 根据层数调用对应生成函数
execute if score @s sd_mine_floor matches 1..40 run function stardew:monsters/spawn/floor_1_40
execute if score @s sd_mine_floor matches 41..80 run function stardew:monsters/spawn/floor_41_80
execute if score @s sd_mine_floor matches 81..100 run function stardew:monsters/spawn/floor_81_100
