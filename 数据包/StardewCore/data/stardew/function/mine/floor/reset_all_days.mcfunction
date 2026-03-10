# stardew:mine/floor/reset_all_days.mcfunction
# 重置所有楼层的访问日期 (用于新的一天开始或手动重置)
# 清空 floor_days 存储，使所有楼层在下次访问时重新生成

data remove storage stardew:mine floor_days
data merge storage stardew:mine {floor_days: {}}

tellraw @a {"text":"[矿洞] 所有楼层已重置！","color":"gold"}
