# stardew:mine/floor/mark_visited_impl.mcfunction
# 宏实现 - 标记楼层访问日期
# 参数: $(floor), $(day)

$data modify storage stardew:mine floor_days.$(floor) set value $(day)
