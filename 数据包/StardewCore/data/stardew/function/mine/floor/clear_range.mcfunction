# stardew:mine/floor/clear_range.mcfunction
# 批量清理楼层范围内的所有实体
# 参数: storage stardew:mine range.start, range.end
# 用于电梯跳层时清理中间所有楼层

# 获取起点和终点
execute store result score #clear_start sd_mine_temp run data get storage stardew:mine range.start
execute store result score #clear_end sd_mine_temp run data get storage stardew:mine range.end

# 确保start < end (如果向上跳层)
scoreboard players operation #clear_min sd_mine_temp = #clear_start sd_mine_temp
scoreboard players operation #clear_max sd_mine_temp = #clear_end sd_mine_temp
execute if score #clear_start sd_mine_temp > #clear_end sd_mine_temp run scoreboard players operation #clear_min sd_mine_temp = #clear_end sd_mine_temp
execute if score #clear_start sd_mine_temp > #clear_end sd_mine_temp run scoreboard players operation #clear_max sd_mine_temp = #clear_start sd_mine_temp

# 循环清理所有层 (从min到max)
function stardew:mine/floor/clear_range_loop

tellraw @s [{"text":"[矿井] ","color":"gray"},{"text":"已清理第","color":"white"},{"score":{"name":"#clear_min","objective":"sd_mine_temp"},"color":"gold"},{"text":"至","color":"white"},{"score":{"name":"#clear_max","objective":"sd_mine_temp"},"color":"gold"},{"text":"层的旧实体","color":"white"}]
