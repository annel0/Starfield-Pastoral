# stardew:mine/floor/clear_range_smart_loop.mcfunction
# 智能递归清理: 只清理需要刷新的楼层
# 使用 #clear_min 作为当前层, #clear_max 为终点

# 如果当前层 > 最大层则停止
execute if score #clear_min sd_mine_temp > #clear_max sd_mine_temp run return 0

# 检查该层是否需要刷新 (通过访问天数判断)
execute store result storage stardew:mine check.floor int 1 run scoreboard players get #clear_min sd_mine_temp
function stardew:mine/floor/check_if_need_clear with storage stardew:mine check

# 如果需要清理 (#need_clear = 1) 则执行清理
execute if score #need_clear sd_mine_temp matches 1 run function stardew:mine/floor/clear_single_floor

# 递增并继续
scoreboard players add #clear_min sd_mine_temp 1
function stardew:mine/floor/clear_range_smart_loop
