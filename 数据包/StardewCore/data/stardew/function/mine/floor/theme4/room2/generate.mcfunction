# stardew:mine/floor/theme4/room2/generate.mcfunction
# 生成 theme4 room2 (40x5x40)
# 参数: $(z)
# 梯子: X=3 Z+4, 电梯: X=3 Z+2
# 特点: 有 stripped_oak_log，玩家面朝正东

# ===== 计算必要的Z坐标偏移 =====
execute store result score #base_z sd_mine_temp run data get storage stardew:mine gen.z

# z+2 (电梯位置)
scoreboard players operation #z2 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z2 sd_mine_temp 2
execute store result storage stardew:mine room.z2 int 1 run scoreboard players get #z2 sd_mine_temp

# z+4 (梯子位置)
scoreboard players operation #z4 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z4 sd_mine_temp 4
execute store result storage stardew:mine room.z4 int 1 run scoreboard players get #z4 sd_mine_temp

# z+6 (矿石区域起始)
scoreboard players operation #z6 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z6 sd_mine_temp 6

# z+36 (矿石区域结束，40格房间留4格边界)
scoreboard players operation #z36 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z36 sd_mine_temp 36

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme4/room2 0 64 $(z)

# ===== 旋转 stripped_oak_log 方块朝向（面向东方，axis=x） =====
# 遍历整个房间区域，将所有 stripped_oak_log[axis=z] 改为 axis=x
$execute in stardew:mine run fill 0 64 $(z) 40 68 $(z) stripped_oak_log[axis=x] replace stripped_oak_log[axis=z]

# ===== 传送玩家到梯子位置，面朝正东 (yaw=90) =====
function stardew:mine/floor/theme4/room2/teleport with storage stardew:mine room

# ===== 生成出口梯子 (X=3, Z+4) =====
function stardew:mine/floor/theme4/room2/spawn_exit with storage stardew:mine room

# ===== 每5层生成电梯 (X=3, Z+2) =====
scoreboard players operation #check_elevator sd_mine_temp = @s sd_mine_floor
scoreboard players operation #check_elevator sd_mine_temp %= #5 sd_const
execute if score #check_elevator sd_mine_temp matches 0 run function stardew:mine/floor/theme4/room2/spawn_elevator with storage stardew:mine room

# ===== 生成矿石 (使用统一的矿石生成系统) =====
# 矿物区域: X:4~36 (40格房间，左右各留2格), Z:z+6~z+36 (前后各留2-4格)
scoreboard players set #room_x_min sd_mine_temp 4
scoreboard players set #room_x_max sd_mine_temp 36
scoreboard players operation #room_z_min sd_mine_temp = #z6 sd_mine_temp
scoreboard players operation #room_z_max sd_mine_temp = #z36 sd_mine_temp
function stardew:mine/floor/spawn_stones_direct
