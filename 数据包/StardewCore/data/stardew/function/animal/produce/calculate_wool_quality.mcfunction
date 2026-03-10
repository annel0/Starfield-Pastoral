# ================================================================
# 星露谷物语 - 计算羊毛品质
# ================================================================
# 用途：根据友好度计算羊毛品质（Base/Silver/Gold/Diamond）
# 调用：从 check_single_rabbit.mcfunction 调用
# 
# 逻辑与鸡蛋相同：
# - Base: 默认
# - Silver: 友好度 >= 200
# - Gold: 友好度 >= 900
# - Diamond: 友好度 == 1000

# 设置基础 CMD（8016 = wool_base）
scoreboard players set #produce_cmd stardew.temp 8016

# 根据友好度升级品质
execute if score @s stardew.animal.friendship matches 200.. run scoreboard players set #produce_cmd stardew.temp 8017
execute if score @s stardew.animal.friendship matches 900.. run scoreboard players set #produce_cmd stardew.temp 8018
execute if score @s stardew.animal.friendship matches 1000 run scoreboard players set #produce_cmd stardew.temp 8019
