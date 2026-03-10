# ================================================================
# 星露谷物语 - 生成兔子产物
# ================================================================
# 用途：根据类型和品质生成兔子产物（羊毛或兔子脚）
# @s = 兔子逻辑实体

# 如果是羊毛，使用 calculate_wool_quality 计算的 CMD（8016-8019）
# 如果是兔子脚，覆盖 CMD
execute if score @s stardew.temp.is_rabbit_foot matches 1 run scoreboard players set #produce_cmd stardew.temp 8020

# 如果是兔子脚，也根据友好度设置品质
execute if score @s stardew.temp.is_rabbit_foot matches 1 if score @s stardew.animal.friendship matches 200.. run scoreboard players set #produce_cmd stardew.temp 8021
execute if score @s stardew.temp.is_rabbit_foot matches 1 if score @s stardew.animal.friendship matches 900.. run scoreboard players set #produce_cmd stardew.temp 8022
execute if score @s stardew.temp.is_rabbit_foot matches 1 if score @s stardew.animal.friendship matches 1000 run scoreboard players set #produce_cmd stardew.temp 8023

# 将 #produce_cmd 复制到 #egg_cmd（使用现有的生成系统）
scoreboard players operation #egg_cmd stardew.animal.temp = #produce_cmd stardew.temp

# 设置视觉模型 CMD（羊毛 108，兔子脚 109）
scoreboard players set #visual_cmd stardew.animal.temp 108
execute if score @s stardew.temp.is_rabbit_foot matches 1 run scoreboard players set #visual_cmd stardew.animal.temp 109

# 如果兔子在建筑内，在建筑位置生成
execute if score @s stardew.animal.building matches 1.. run function stardew:animal/produce/spawn_egg_at_building

# 否则在兔子脚下生成
execute unless score @s stardew.animal.building matches 1.. at @s run function stardew:animal/produce/spawn_egg_at_position
