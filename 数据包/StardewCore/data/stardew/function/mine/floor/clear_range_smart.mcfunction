# stardew:mine/floor/clear_range_smart.mcfunction
# 智能批量清理: 只清理"需要刷新的旧层",保留"今天已访问的层"
# 参数: storage stardew:mine range.start, range.end
# 用于电梯跳层时清理过期楼层

# 获取起点和终点
execute store result score #clear_start sd_mine_temp run data get storage stardew:mine range.start
execute store result score #clear_end sd_mine_temp run data get storage stardew:mine range.end

# 确保start < end
scoreboard players operation #clear_min sd_mine_temp = #clear_start sd_mine_temp
scoreboard players operation #clear_max sd_mine_temp = #clear_end sd_mine_temp
execute if score #clear_start sd_mine_temp > #clear_end sd_mine_temp run scoreboard players operation #clear_min sd_mine_temp = #clear_end sd_mine_temp
execute if score #clear_start sd_mine_temp > #clear_end sd_mine_temp run scoreboard players operation #clear_max sd_mine_temp = #clear_start sd_mine_temp

# 获取当前游戏天数
scoreboard players operation #current_day sd_mine_temp = Global sd_day

# 记录清理的层数
scoreboard players set #cleared_count sd_mine_temp 0

# 循环检查并清理所有层
function stardew:mine/floor/clear_range_smart_loop
