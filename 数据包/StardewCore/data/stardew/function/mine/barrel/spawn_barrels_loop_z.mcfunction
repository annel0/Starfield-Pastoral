# stardew:mine/barrel/spawn_barrels_loop_z.mcfunction
# Z 循环遍历，检查每个位置
# 执行者: 玩家 (@s)

# 将坐标存入storage 用于 macro
execute store result storage stardew:mine barrel.x int 1 run scoreboard players get #barrel_x sd_mine_temp
execute store result storage stardew:mine barrel.z int 1 run scoreboard players get #barrel_z sd_mine_temp

# 检查该位置并尝试生成木桶
function stardew:mine/barrel/check_and_spawn with storage stardew:mine barrel

# Z 坐标 +1
scoreboard players add #barrel_z sd_mine_temp 1

# 检查是否继续 Z 循环
execute if score #barrel_z sd_mine_temp <= #room_z_max sd_mine_temp run function stardew:mine/barrel/spawn_barrels_loop_z
