# stardew:mine/floor/generate_room_impl.mcfunction
# 宏函数：实际生成房间
# 参数: $(floor), $(z), $(z4), $(z5), $(z6), $(z15), $(z24), $(z30)

#
# 主题分布:
# - 1-25层: theme1
# - 26-50层: theme2
# - 51-75层: theme3
# - 76-100层: theme4
#
# 每个主题支持5种房间:
# - room1 (30x5x30): 梯子X=3, 电梯X=4
# - room2 (30x5x40): 梯子X=6, 电梯X=7
# - room3 (40x5x40): 梯子X=4, 电梯X=6
# - room4 (40x5x40): 梯子X=19, 电梯X=20
# - room5 (30x5x20): 梯子X=26, 电梯X=25

# ===== 确定主题 =====
scoreboard players set #theme sd_mine_temp 1
execute if score @s sd_mine_floor matches 26..50 run scoreboard players set #theme sd_mine_temp 2
execute if score @s sd_mine_floor matches 51..75 run scoreboard players set #theme sd_mine_temp 3
execute if score @s sd_mine_floor matches 76..100 run scoreboard players set #theme sd_mine_temp 4

# ===== 检查是否为特殊层 =====
# 第25层: treasure_room (room_type = 0)
# 第50层: theme2 treasure_room (待实现)
# 第75层: theme3 treasure_room (待实现)
# 第100层: theme4 treasure_room (待实现)
scoreboard players set #is_special_floor sd_mine_temp 0
execute if score @s sd_mine_floor matches 25 run scoreboard players set #is_special_floor sd_mine_temp 1
execute if score @s sd_mine_floor matches 50 run scoreboard players set #is_special_floor sd_mine_temp 1
execute if score @s sd_mine_floor matches 75 run scoreboard players set #is_special_floor sd_mine_temp 1
execute if score @s sd_mine_floor matches 100 run scoreboard players set #is_special_floor sd_mine_temp 1

# ===== 随机选择房间类型 =====
# room_type: 0=treasure_room, 1=room1, 2=room2, 3=room3, 4=room4, 5=room5
# 特殊层固定使用 treasure_room (room_type = 0)
scoreboard players set #room_type sd_mine_temp 0
execute if score #is_special_floor sd_mine_temp matches 0 store result score #rand_seed sd_mine_temp run time query gametime
execute if score #is_special_floor sd_mine_temp matches 0 run scoreboard players operation #rand_seed sd_mine_temp += @s sd_mine_floor
execute if score #is_special_floor sd_mine_temp matches 0 run scoreboard players operation #rand_seed sd_mine_temp %= #5 sd_const
execute if score #is_special_floor sd_mine_temp matches 0 run scoreboard players add #rand_seed sd_mine_temp 1
execute if score #is_special_floor sd_mine_temp matches 0 run scoreboard players operation #room_type sd_mine_temp = #rand_seed sd_mine_temp

# ===== 统一清空整个区域 (使用最大尺寸 50x5x50，因为theme4/room5是50x50) =====
execute store result score #base_z sd_mine_temp run data get storage stardew:mine gen.z
scoreboard players operation #z50_clear sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z50_clear sd_mine_temp 50
execute store result storage stardew:mine gen.z50 int 1 run scoreboard players get #z50_clear sd_mine_temp

# 只清空方块，不清理实体（实体会在玩家到达后清理）
function stardew:mine/floor/clear_area with storage stardew:mine gen

# ===== 根据主题和房间类型生成 =====
# theme1 (1-25层)
# 第25层特殊: treasure_room (只生成宝箱层，不生成其他房间)
execute if score @s sd_mine_floor matches 25 run function stardew:mine/floor/theme1/treasure_room/generate with storage stardew:mine gen
execute if score @s sd_mine_floor matches 25 run return 0

# 普通层
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme1/room1/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme1/room2/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme1/room3/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme1/room4/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 1 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme1/room5/generate with storage stardew:mine gen

# theme2 (26-50层)
# 第50层特殊: treasure_room (只生成宝箱层，不生成其他房间)
execute if score @s sd_mine_floor matches 50 run function stardew:mine/floor/theme2/treasure_room/generate with storage stardew:mine gen
execute if score @s sd_mine_floor matches 50 run return 0

# 普通层
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme2/room1/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme2/room2/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme2/room3/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme2/room4/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 2 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme2/room5/generate with storage stardew:mine gen

# theme3 (51-75层)
# 第75层特殊: treasure_room (只生成宝箱层，不生成其他房间)
execute if score @s sd_mine_floor matches 75 run function stardew:mine/floor/theme3/treasure_room/generate with storage stardew:mine gen
execute if score @s sd_mine_floor matches 75 run return 0

# 普通层
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme3/room1/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme3/room2/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme3/room3/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme3/room4/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 3 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme3/room5/generate with storage stardew:mine gen

# theme4 (76-100层)
# 第100层特殊: treasure_room (矿洞最深处，只生成宝箱层，不生成其他房间)
execute if score @s sd_mine_floor matches 100 run function stardew:mine/floor/theme4/treasure_room/generate with storage stardew:mine gen
execute if score @s sd_mine_floor matches 100 run return 0

# 普通层
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 1 run function stardew:mine/floor/theme4/room1/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 2 run function stardew:mine/floor/theme4/room2/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 3 run function stardew:mine/floor/theme4/room3/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 4 run function stardew:mine/floor/theme4/room4/generate with storage stardew:mine gen
execute if score #theme sd_mine_temp matches 4 if score #room_type sd_mine_temp matches 5 run function stardew:mine/floor/theme4/room5/generate with storage stardew:mine gen

# ===== 保存该层的主题和房间类型 =====
execute store result storage stardew:mine save.floor int 1 run scoreboard players get @s sd_mine_floor
execute store result storage stardew:mine save.room_type int 1 run scoreboard players get #room_type sd_mine_temp
execute store result storage stardew:mine save.theme int 1 run scoreboard players get #theme sd_mine_temp
function stardew:mine/floor/save_room_type with storage stardew:mine save
