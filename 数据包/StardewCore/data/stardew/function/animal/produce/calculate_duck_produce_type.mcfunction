# ================================================================
# 星露谷物语 - 鸭子产物类型判定
# ================================================================
# 用途：根据友好度和心情判定鸭子产鸭蛋还是鸭毛
# 调用：从 check_single_duck.mcfunction 调用
# 
# 逻辑：
# - 基础产物：鸭蛋（stardew.temp.is_feather = 0）
# - 特殊产物：鸭毛（stardew.temp.is_feather = 1）
# - 产鸭毛条件：友好度 >= 200 且心情 >= 150
# - 满足条件后有概率产鸭毛而非鸭蛋

# 默认产鸭蛋
scoreboard players set @s stardew.temp.is_feather 0

# 检查是否满足产鸭毛的基础条件
scoreboard players set #can_feather stardew.animal.temp 0
execute if score @s stardew.animal.friendship matches 200.. if score @s stardew.animal.mood matches 150.. run scoreboard players set #can_feather stardew.animal.temp 1

# 如果满足条件，有概率产鸭毛
# 根据Wiki：当满足条件时，有一定概率产鸭毛
# 这里设定为30%的概率（可调整）
execute if score #can_feather stardew.animal.temp matches 1 store result score #feather_chance stardew.animal.temp run random value 1..100
execute if score #can_feather stardew.animal.temp matches 1 if score #feather_chance stardew.animal.temp matches ..30 run scoreboard players set @s stardew.temp.is_feather 1
