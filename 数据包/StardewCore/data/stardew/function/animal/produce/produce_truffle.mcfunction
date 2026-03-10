# ================================================================
# 星露谷物语 - 生成松露实体
# ================================================================
# 用途：在猪所在的畜棚内生成松露实体
# 调用：从 check_single_pig.mcfunction 调用

# 根据品质确定 CMD
# 松露：8040(base), 8041(silver), 8042(gold), 8043(diamond)

# 初始化 CMD 为 8040
scoreboard players set #truffle_cmd stardew.animal.temp 8040

# 根据品质调整 CMD
execute if score @s stardew.temp.quality matches 1 run scoreboard players add #truffle_cmd stardew.animal.temp 1
execute if score @s stardew.temp.quality matches 2 run scoreboard players add #truffle_cmd stardew.animal.temp 2
execute if score @s stardew.temp.quality matches 3 run scoreboard players add #truffle_cmd stardew.animal.temp 3

# 确定视觉模型 CMD（松露使用 CMD 110）
scoreboard players set #visual_cmd stardew.animal.temp 110

# 如果猪有 building ID，则在建筑中心附近生成
execute if score @s stardew.animal.building matches 1.. run function stardew:animal/produce/spawn_truffle_at_building

# 如果猪没有建筑 ID，则在猪当前位置附近生成（后备方案）
execute unless score @s stardew.animal.building matches 1.. at @s run function stardew:animal/produce/spawn_truffle_at_position