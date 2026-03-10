# stardew:mine/floor/theme2/room1/generate.mcfunction
# 生成 theme2/room1 (40x5x40) - 冰川主题
# 参数: $(z), $(z6)
# 玩家出生/梯子: X=6, Z=z+3, 电梯: X=7, Z=z+3

# 计算 room1 特有的 Z 偏移
execute store result score #base_z sd_mine_temp run data get storage stardew:mine gen.z
scoreboard players operation #z3 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z3 sd_mine_temp 3
scoreboard players operation #z34 sd_mine_temp = #base_z sd_mine_temp
scoreboard players add #z34 sd_mine_temp 34

execute store result storage stardew:mine room.z3 int 1 run scoreboard players get #z3 sd_mine_temp

# ===== 放置结构 =====
$execute in stardew:mine run place template stardew:mine/theme2/room1 0 64 $(z)

# ===== 传送玩家 =====
function stardew:mine/floor/theme2/room1/teleport with storage stardew:mine room

# ===== 生成出口梯子 =====
function stardew:mine/floor/theme2/room1/spawn_exit with storage stardew:mine room

# ===== 每5层生成电梯 =====
scoreboard players operation #check_elevator sd_mine_temp = @s sd_mine_floor
scoreboard players operation #check_elevator sd_mine_temp %= #5 sd_const
execute if score #check_elevator sd_mine_temp matches 0 run function stardew:mine/floor/theme2/room1/spawn_elevator with storage stardew:mine room

# ===== 生成矿石 =====
scoreboard players set #room_x_min sd_mine_temp 6
scoreboard players set #room_x_max sd_mine_temp 34
$scoreboard players set #room_z_min sd_mine_temp $(z6)
scoreboard players operation #room_z_max sd_mine_temp = #z34 sd_mine_temp
function stardew:mine/floor/spawn_stones_direct
