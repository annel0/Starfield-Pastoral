# stardew:mine/barrel/spawn_barrels.mcfunction
# 遍历房间生成木桶
# 前置: #room_x_min, #room_x_max, #room_z_min, #room_z_max 已设置
# 执行者: 玩家 (@s)

# 使用房间已设置的 X 和 Z 范围

# 初始化循环变量
scoreboard players operation #barrel_x sd_mine_temp = #room_x_min sd_mine_temp
scoreboard players operation #barrel_z sd_mine_temp = #room_z_min sd_mine_temp

# 开始 X 循环
function stardew:mine/barrel/spawn_barrels_loop_x
