# stardew:mine/floor/check_if_need_clear.mcfunction
# 检查指定楼层是否需要清理 (通过访问天数判断)
# 参数: $(floor)
# 输出: #need_clear sd_mine_temp (1=需要清理, 0=保留)

# 默认需要清理
scoreboard players set #need_clear sd_mine_temp 1

# 初始化楼层天数为-1 (表示未访问)
scoreboard players set #floor_day sd_mine_temp -1

# 获取该层的上次访问天数
$execute if score Floor$(floor) sd_mine_visited matches 1.. run scoreboard players operation #floor_day sd_mine_temp = Floor$(floor) sd_mine_visited

# 如果该层今天已访问 (访问天数 = 当前天数) 则不清理
execute if score #floor_day sd_mine_temp = #current_day sd_mine_temp run scoreboard players set #need_clear sd_mine_temp 0
