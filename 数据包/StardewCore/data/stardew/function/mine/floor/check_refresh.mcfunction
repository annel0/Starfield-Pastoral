# stardew:mine/floor/check_refresh.mcfunction
# 检查楼层是否需要刷新
# 执行者: 玩家 (@s)
# 前置: sd_mine_floor 已设置为目标层数
# 输出: #need_refresh sd_mine_temp (1=需要刷新, 0=不需要)

# 默认需要刷新
scoreboard players set #need_refresh sd_mine_temp 1

# 获取当前游戏天数 (使用星露谷的 sd_day 而不是原版 time query day)
scoreboard players operation #current_day sd_mine_temp = Global sd_day

# 获取该层上次访问的天数 (需要用宏)
execute store result storage stardew:mine check.floor int 1 run scoreboard players get @s sd_mine_floor
function stardew:mine/floor/check_refresh_impl with storage stardew:mine check
