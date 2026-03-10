# ================================================================
# 星露谷物语 - 设置牛奶已准备好
# ================================================================
# 用途：计算牛奶的CMD并保存到牛的数据中
# 调用：从 check_single_cow.mcfunction 调用

# 根据 is_large 和 quality 确定 CMD
# 普通牛奶：8024(base), 8025(silver), 8026(gold), 8027(diamond)
# 大桶牛奶：8028(base), 8029(silver), 8030(gold), 8031(diamond)

# 初始化 CMD 为 8024
scoreboard players set #milk_cmd stardew.animal.temp 8024

# 如果是大桶奶，CMD + 4
execute if score @s stardew.temp.is_large matches 1 run scoreboard players add #milk_cmd stardew.animal.temp 4

# 根据品质调整 CMD
execute if score @s stardew.temp.quality matches 1 run scoreboard players add #milk_cmd stardew.animal.temp 1
execute if score @s stardew.temp.quality matches 2 run scoreboard players add #milk_cmd stardew.animal.temp 2
execute if score @s stardew.temp.quality matches 3 run scoreboard players add #milk_cmd stardew.animal.temp 3
