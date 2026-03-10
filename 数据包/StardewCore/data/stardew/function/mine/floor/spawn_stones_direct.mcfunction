# stardew:mine/floor/spawn_stones_direct.mcfunction
# 在房间内随机生成矿石 (直接使用 scoreboard 中的 Z 范围)
# 前置: #room_z_min 和 #room_z_max 已设置
# 执行者: 玩家 (@s)

# 重置梯子状态和石头计数
scoreboard players set @s sd_mine_ladder 0
scoreboard players set @s sd_mine_stones 0

# Z 范围已在 #room_z_min 和 #room_z_max 中

# 生成 40 个石头
scoreboard players set #spawn_count sd_mine_temp 40
function stardew:mine/floor/spawn_stone_loop

# 计算实际生成的石头数量 (通过计数带 sd_mine_stone 标签的实体)
scoreboard players operation #room_center_z sd_mine_temp = #room_z_min sd_mine_temp
scoreboard players operation #room_center_z sd_mine_temp += #room_z_max sd_mine_temp
scoreboard players operation #room_center_z sd_mine_temp /= #2 sd_const
execute store result storage stardew:mine count.z int 1 run scoreboard players get #room_center_z sd_mine_temp
function stardew:mine/floor/count_stones with storage stardew:mine count

# 生成木桶 (遍历房间内所有stripped_oak_log)
function stardew:mine/barrel/spawn_barrels
