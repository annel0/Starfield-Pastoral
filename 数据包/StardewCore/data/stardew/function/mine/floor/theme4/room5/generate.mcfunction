# stardew:mine/floor/theme4/room5/generate.mcfunction
# 生成 theme4 room5 (50x5x50)
# 参数: $(z)
# 梯子: X=18 Z+2, 电梯: X=20 Z+2
# 特点: 有 stripped_oak_log

# ===== 计算必要的Z坐标偏移 =====
execute store result score #base_z sd_mine_temp run data get storage stardew:mine gen.z

# z+2 (梯子和电梯位置)
scoreboard players operation #z2 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z2 sd_mine_temp 2
execute store result storage stardew:mine room.z2 int 1 run scoreboard players get #z2 sd_mine_temp

# z+5 (矿石区域起始，留3格边界)
scoreboard players operation #z5 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z5 sd_mine_temp 5

# z+45 (矿石区域结束，50格房间留5格边界)
scoreboard players operation #z45 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z45 sd_mine_temp 45

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme4/room5 0 64 $(z)

# ===== 传送玩家到梯子位置 =====
function stardew:mine/floor/theme4/room5/teleport with storage stardew:mine room

# ===== 生成出口梯子 (X=18, Z+2) =====
function stardew:mine/floor/theme4/room5/spawn_exit with storage stardew:mine room

# ===== 每5层生成电梯 (X=20, Z+2) =====
scoreboard players operation #check_elevator sd_mine_temp = @s sd_mine_floor
scoreboard players operation #check_elevator sd_mine_temp %= #5 sd_const
execute if score #check_elevator sd_mine_temp matches 0 run function stardew:mine/floor/theme4/room5/spawn_elevator with storage stardew:mine room

# ===== 生成矿石 (使用统一的矿石生成系统) =====
# 矿物区域: X:5~45 (50格房间，左右各留2-5格), Z:z+5~z+45 (前后各留5格)
scoreboard players set #room_x_min sd_mine_temp 5
scoreboard players set #room_x_max sd_mine_temp 45
scoreboard players operation #room_z_min sd_mine_temp = #z5 sd_mine_temp
scoreboard players operation #room_z_max sd_mine_temp = #z45 sd_mine_temp
function stardew:mine/floor/spawn_stones_direct

# 计算矿石生成区域 (留出边界3格)
scoreboard players operation #room_x_min int = #x int
scoreboard players operation #room_x_max int = #x int
scoreboard players operation #room_z_min int = #z int
scoreboard players operation #room_z_max int = #z int

scoreboard players add #room_x_min int 3
scoreboard players add #room_x_max int 47
scoreboard players add #room_z_min int 3
scoreboard players add #room_z_max int 47

# 生成矿石
function stardew:mine/floor/spawn_stones_direct
