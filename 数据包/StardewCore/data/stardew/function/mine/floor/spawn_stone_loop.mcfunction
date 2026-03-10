# stardew:mine/floor/spawn_stone_loop.mcfunction
# 循环生成石头
# 使用随机坐标
# 前置: #room_x_min, #room_x_max, #room_z_min, #room_z_max 已设置

# 检查是否还需要生成
execute unless score #spawn_count sd_mine_temp matches 1.. run return 0

# 随机 X 坐标 (在 x_min 到 x_max 范围内)
scoreboard players operation #x_range sd_mine_temp = #room_x_max sd_mine_temp
scoreboard players operation #x_range sd_mine_temp -= #room_x_min sd_mine_temp
execute store result score #rand_offset_x sd_mine_temp run random value 0..1000
scoreboard players operation #rand_offset_x sd_mine_temp %= #x_range sd_mine_temp
scoreboard players operation #rand_x sd_mine_temp = #room_x_min sd_mine_temp
scoreboard players operation #rand_x sd_mine_temp += #rand_offset_x sd_mine_temp

# 随机 Z 坐标 (在 z_min 到 z_max 范围内)
scoreboard players operation #z_range sd_mine_temp = #room_z_max sd_mine_temp
scoreboard players operation #z_range sd_mine_temp -= #room_z_min sd_mine_temp
execute store result score #rand_offset_z sd_mine_temp run random value 0..1000
scoreboard players operation #rand_offset_z sd_mine_temp %= #z_range sd_mine_temp
scoreboard players operation #rand_z sd_mine_temp = #room_z_min sd_mine_temp
scoreboard players operation #rand_z sd_mine_temp += #rand_offset_z sd_mine_temp

# 将随机值存入 storage
execute store result storage stardew:mine spawn.x int 1 run scoreboard players get #rand_x sd_mine_temp
execute store result storage stardew:mine spawn.z int 1 run scoreboard players get #rand_z sd_mine_temp

# 尝试在该位置生成石头
function stardew:mine/floor/try_spawn_stone with storage stardew:mine spawn

# 减少计数
scoreboard players remove #spawn_count sd_mine_temp 1

# 继续循环
function stardew:mine/floor/spawn_stone_loop
