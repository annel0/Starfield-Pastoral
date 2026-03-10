# stardew:mine/floor/theme3/room1/generate.mcfunction
# 生成 theme3 room1 (30x5x30)
# 参数: $(z)
# 玩家出生/梯子: X=12 Z+1, 电梯: X=13 Z+1

# ===== 计算必要的Z坐标偏移 =====
execute store result score #base_z sd_mine_temp run data get storage stardew:mine gen.z

# z+1 (梯子和电梯位置)
scoreboard players operation #z1 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z1 sd_mine_temp 1
execute store result storage stardew:mine room.z1 int 1 run scoreboard players get #z1 sd_mine_temp

# z+4 (矿石区域起始)
scoreboard players operation #z4 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z4 sd_mine_temp 4

# z+26 (矿石区域结束，30格房间留4格边界)
scoreboard players operation #z26 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z26 sd_mine_temp 26

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme3/room1 0 64 $(z)

# ===== 传送玩家到梯子位置 =====
function stardew:mine/floor/theme3/room1/teleport with storage stardew:mine room

# ===== 生成出口梯子 (X=12, Z+1) =====
function stardew:mine/floor/theme3/room1/spawn_exit with storage stardew:mine room

# ===== 每5层生成电梯 =====
scoreboard players operation #check_elevator sd_mine_temp = @s sd_mine_floor
scoreboard players operation #check_elevator sd_mine_temp %= #5 sd_const
execute if score #check_elevator sd_mine_temp matches 0 run function stardew:mine/floor/theme3/room1/spawn_elevator with storage stardew:mine room

# ===== 生成矿石 (使用统一的矿石生成系统，会自动根据层数调用 roll_theme3) =====
# 矿物区域: X:4~26 (30格房间，左右各留2格), Z:z+4~z+26 (前后各留2-4格)
scoreboard players set #room_x_min sd_mine_temp 4
scoreboard players set #room_x_max sd_mine_temp 26
scoreboard players operation #room_z_min sd_mine_temp = #z4 sd_mine_temp
scoreboard players operation #room_z_max sd_mine_temp = #z26 sd_mine_temp
function stardew:mine/floor/spawn_stones_direct
