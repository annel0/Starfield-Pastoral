# stardew:mine/floor/clear_single_floor.mcfunction
# 清理单个楼层
# 使用 #clear_min 作为当前层数

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

# 清理计数+1
scoreboard players add #cleared_count sd_mine_temp 1
