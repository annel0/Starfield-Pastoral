# stardew:mine/floor/theme1/room1/generate.mcfunction
# 生成 theme1/room1 (30x5x30)
# 参数: $(z), $(z4), $(z5), $(z6), $(z24), $(z30)
# 梯子: X=3, 电梯: X=4, 玩家出生: X=3

# 计算并存储 z4 用于子函数
execute store result storage stardew:mine room.z4 int 1 run data get storage stardew:mine gen.z4

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme1/room1 0 64 $(z)

# ===== 传送玩家 =====
$execute in stardew:mine run tp @s 3 65 $(z5) 180 0

# ===== 生成出口梯子 =====
function stardew:mine/floor/theme1/room1/spawn_exit with storage stardew:mine room

# ===== 每5层生成电梯 =====
scoreboard players operation #check_elevator sd_mine_temp = @s sd_mine_floor
scoreboard players operation #check_elevator sd_mine_temp %= #5 sd_const
execute if score #check_elevator sd_mine_temp matches 0 run function stardew:mine/floor/theme1/room1/spawn_elevator with storage stardew:mine room

# ===== 生成矿石 =====
# 矿物区域: X:6~24, Z:z+6~z+24
scoreboard players set #room_x_min sd_mine_temp 6
scoreboard players set #room_x_max sd_mine_temp 24
$scoreboard players set #room_z_min sd_mine_temp $(z6)
$scoreboard players set #room_z_max sd_mine_temp $(z24)
function stardew:mine/floor/spawn_stones_direct
