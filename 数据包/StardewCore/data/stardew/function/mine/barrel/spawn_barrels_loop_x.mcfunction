# stardew:mine/barrel/spawn_barrels_loop_x.mcfunction
# X 循环遍历
# 执行者: 玩家 (@s)

# 重置 Z 坐标到起始位置
scoreboard players operation #barrel_z sd_mine_temp = #room_z_min sd_mine_temp

# 开始 Z 循环
function stardew:mine/barrel/spawn_barrels_loop_z

# X 坐标 +1
scoreboard players add #barrel_x sd_mine_temp 1

# 检查是否继续 X 循环
execute if score #barrel_x sd_mine_temp <= #room_x_max sd_mine_temp run function stardew:mine/barrel/spawn_barrels_loop_x
