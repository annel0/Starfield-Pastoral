# ================================================================
# 星露谷物语 - 设置羊毛已准备好
# ================================================================
# 用途：计算羊毛的CMD并保存到绵羊的数据中
# 调用：从 check_single_sheep.mcfunction 调用

# 根据 quality 确定 CMD
# 羊毛：8032(普通), 8033(银星), 8034(金星), 8035(铱星)

# 初始化 CMD 为 8032
scoreboard players set #wool_cmd stardew.animal.temp 8032

# 根据品质调整 CMD
execute if score @s stardew.temp.quality matches 1 run scoreboard players add #wool_cmd stardew.animal.temp 1
execute if score @s stardew.temp.quality matches 2 run scoreboard players add #wool_cmd stardew.animal.temp 2
execute if score @s stardew.temp.quality matches 3 run scoreboard players add #wool_cmd stardew.animal.temp 3
