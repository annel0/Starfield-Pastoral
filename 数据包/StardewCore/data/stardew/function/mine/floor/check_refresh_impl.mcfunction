# stardew:mine/floor/check_refresh_impl.mcfunction
# 宏实现 - 检查具体楼层的刷新状态
# 参数: $(floor) - 楼层数

# 尝试获取该层的上次访问日期
# 如果该层从未访问过，floor_days.$(floor) 不存在，则默认需要刷新

# 先检查数据是否存在
scoreboard players set #data_exists sd_mine_temp 0
$execute if data storage stardew:mine floor_days.$(floor) run scoreboard players set #data_exists sd_mine_temp 1

# 如果数据不存在，保持需要刷新 (默认值)
execute if score #data_exists sd_mine_temp matches 0 run return 0

# 数据存在，获取该层的访问日期
$execute store result score #floor_day sd_mine_temp run data get storage stardew:mine floor_days.$(floor)

# 如果该层日期等于当前日期，则不需要刷新
execute if score #floor_day sd_mine_temp = #current_day sd_mine_temp run scoreboard players set #need_refresh sd_mine_temp 0
