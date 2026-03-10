# stardew:mine/floor/mark_visited.mcfunction
# 标记楼层为今日已访问
# 执行者: 玩家 (@s)
# 前置: sd_mine_floor 已设置

# 获取当前游戏天数 (使用星露谷的 sd_day)
execute store result storage stardew:mine visit.day int 1 run scoreboard players get Global sd_day
execute store result storage stardew:mine visit.floor int 1 run scoreboard players get @s sd_mine_floor

# 用宏更新该层的访问日期
function stardew:mine/floor/mark_visited_impl with storage stardew:mine visit
