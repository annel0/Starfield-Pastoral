# stardew:mine/floor/theme3/room3/generate.mcfunction
# 生成 theme3 room3 (40x5x40)
# 参数: $(z)
# 梯子: X=19 Z+2, 电梯: X=20 Z+3
# 特点: 有 stripped_oak_log

# ===== 计算必要的Z坐标偏移 =====
execute store result score #base_z sd_mine_temp run data get storage stardew:mine gen.z

# z+2 (梯子位置)
scoreboard players operation #z2 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z2 sd_mine_temp 2
execute store result storage stardew:mine room.z2 int 1 run scoreboard players get #z2 sd_mine_temp

# z+3 (电梯位置)
scoreboard players operation #z3 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z3 sd_mine_temp 3
execute store result storage stardew:mine room.z3 int 1 run scoreboard players get #z3 sd_mine_temp

# z+4 (矿石区域起始)
scoreboard players operation #z4 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z4 sd_mine_temp 4

# z+36 (矿石区域结束，40格房间留4格边界)
scoreboard players operation #z36 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z36 sd_mine_temp 36

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme3/room3 0 64 $(z)

# ===== 传送玩家到梯子位置 =====
function stardew:mine/floor/theme3/room3/teleport with storage stardew:mine room

# ===== 生成出口梯子 (X=19, Z+2) =====
function stardew:mine/floor/theme3/room3/spawn_exit with storage stardew:mine room

# ===== 每5层生成电梯 (X=20, Z+3) =====
scoreboard players operation #check_elevator sd_mine_temp = @s sd_mine_floor
scoreboard players operation #check_elevator sd_mine_temp %= #5 sd_const
execute if score #check_elevator sd_mine_temp matches 0 run function stardew:mine/floor/theme3/room3/spawn_elevator with storage stardew:mine room

# ===== 生成矿石 (使用统一的矿石生成系统) =====
# 矿物区域: X:4~36 (40格房间，左右各留2格), Z:z+4~z+36 (前后各留2-4格)
scoreboard players set #room_x_min sd_mine_temp 4
scoreboard players set #room_x_max sd_mine_temp 36
scoreboard players operation #room_z_min sd_mine_temp = #z4 sd_mine_temp
scoreboard players operation #room_z_max sd_mine_temp = #z36 sd_mine_temp
function stardew:mine/floor/spawn_stones_direct
