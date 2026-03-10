# stardew:mine/floor/clear_range_loop.mcfunction
# 递归清理楼层
# 使用 #clear_min 作为当前层, #clear_max 为终点

# 如果当前层 > 最大层则停止
execute if score #clear_min sd_mine_temp > #clear_max sd_mine_temp run return 0

# 清理当前层 (使用clear_entities)
# 计算 z = floor * 100
scoreboard players operation #clear_z sd_mine_temp = #clear_min sd_mine_temp
scoreboard players operation #clear_z sd_mine_temp *= #100 sd_const
# dz = 99 (一个楼层的高度)
scoreboard players set #clear_dz sd_mine_temp 99

# 存储到storage用于宏调用
execute store result storage stardew:mine clear.z int 1 run scoreboard players get #clear_z sd_mine_temp
execute store result storage stardew:mine clear.dz int 1 run scoreboard players get #clear_dz sd_mine_temp

# 调用清理函数
function stardew:mine/floor/clear_entities_impl with storage stardew:mine clear

# 递增并继续
scoreboard players add #clear_min sd_mine_temp 1
function stardew:mine/floor/clear_range_loop
