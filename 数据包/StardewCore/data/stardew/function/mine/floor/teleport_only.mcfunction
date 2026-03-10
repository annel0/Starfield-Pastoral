# stardew:mine/floor/teleport_only.mcfunction
# 仅传送玩家到楼层 (不重新生成)
# 执行者: 玩家 (@s)
# 前置: sd_mine_floor 已设置

# 计算基础 Z 坐标 (层数 × 100)
execute store result score #tp_z sd_mine_temp run scoreboard players get @s sd_mine_floor
scoreboard players operation #tp_z sd_mine_temp *= #100 sd_const

# 获取该层的房间类型和主题
execute store result storage stardew:mine query.floor int 1 run scoreboard players get @s sd_mine_floor
function stardew:mine/floor/get_room_type with storage stardew:mine query

# 存储基础 Z 用于计算
execute store result storage stardew:mine tp.base_z int 1 run scoreboard players get #tp_z sd_mine_temp

# 计算各房间的传送 Z 偏移
scoreboard players operation #tp_z1 sd_mine_temp = #tp_z sd_mine_temp
scoreboard players add #tp_z1 sd_mine_temp 1
scoreboard players operation #tp_z2 sd_mine_temp = #tp_z sd_mine_temp
scoreboard players add #tp_z2 sd_mine_temp 2
scoreboard players operation #tp_z3 sd_mine_temp = #tp_z sd_mine_temp
scoreboard players add #tp_z3 sd_mine_temp 3
scoreboard players operation #tp_z4 sd_mine_temp = #tp_z sd_mine_temp
scoreboard players add #tp_z4 sd_mine_temp 4
scoreboard players operation #tp_z5 sd_mine_temp = #tp_z sd_mine_temp
scoreboard players add #tp_z5 sd_mine_temp 5

execute store result storage stardew:mine tp.z1 int 1 run scoreboard players get #tp_z1 sd_mine_temp
execute store result storage stardew:mine tp.z2 int 1 run scoreboard players get #tp_z2 sd_mine_temp
execute store result storage stardew:mine tp.z3 int 1 run scoreboard players get #tp_z3 sd_mine_temp
execute store result storage stardew:mine tp.z4 int 1 run scoreboard players get #tp_z4 sd_mine_temp
execute store result storage stardew:mine tp.z5 int 1 run scoreboard players get #tp_z5 sd_mine_temp

# 根据主题和房间类型传送
# theme1 (1-25层)
# treasure_room (room_type=0)
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 0 run function stardew:mine/floor/theme1/treasure_room/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme1/room1/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme1/room2/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme1/room3/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme1/room4/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme1/room5/teleport with storage stardew:mine tp

# theme2 (26-50层) - 待实现，暂时使用theme1
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme1/room1/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme1/room2/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme1/room3/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme1/room4/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme1/room5/teleport with storage stardew:mine tp

# theme3 (51-75层) - 待实现，暂时使用theme1
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme1/room1/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme1/room2/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme1/room3/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme1/room4/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme1/room5/teleport with storage stardew:mine tp

# theme4 (76-100层) - 待实现，暂时使用theme1
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme1/room1/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme1/room2/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme1/room3/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme1/room4/teleport with storage stardew:mine tp
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme1/room5/teleport with storage stardew:mine tp

# 计算房间中心 Z (z + 20) 用于后续检测
scoreboard players operation #center_z sd_mine_temp = #tp_z sd_mine_temp
scoreboard players add #center_z sd_mine_temp 20
execute store result storage stardew:mine count.z int 1 run scoreboard players get #center_z sd_mine_temp

# 检测该层是否已有下层坑 + 计算剩余石头数量 (在传送后执行)
function stardew:mine/floor/sync_floor_state with storage stardew:mine count
