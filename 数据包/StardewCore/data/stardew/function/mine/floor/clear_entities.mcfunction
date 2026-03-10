# stardew:mine/floor/clear_entities.mcfunction
# 清空楼层内的所有矿洞相关实体
# 参数: $(z), $(z50) - 房间起始和结束 Z 坐标

# 计算 dz (z50 - z)
execute store result score #clear_z sd_mine_temp run data get storage stardew:mine gen.z
execute store result score #clear_z50 sd_mine_temp run data get storage stardew:mine gen.z50
scoreboard players operation #clear_dz sd_mine_temp = #clear_z50 sd_mine_temp
scoreboard players operation #clear_dz sd_mine_temp -= #clear_z sd_mine_temp

# 存储到 storage
execute store result storage stardew:mine clear.z int 1 run scoreboard players get #clear_z sd_mine_temp
execute store result storage stardew:mine clear.dz int 1 run scoreboard players get #clear_dz sd_mine_temp

# 调用实际清理函数
function stardew:mine/floor/clear_entities_impl with storage stardew:mine clear
