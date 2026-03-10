# ================================================================
# 星露谷物语 - 生成鸡蛋实体
# ================================================================
# 用途：在鸡所在的鸡舍内生成鸡蛋实体
# 调用：从 check_single_chicken.mcfunction 调用

# 根据 is_large 和 quality 确定 CMD
# 普通鸡蛋：8000(base), 8001(silver), 8002(gold), 8003(diamond)
# 大鸡蛋：8004(base), 8005(silver), 8006(gold), 8007(diamond)

# 初始化 CMD 为 8000
scoreboard players set #egg_cmd stardew.animal.temp 8000

# 如果是大鸡蛋，CMD + 4
execute if score @s stardew.temp.is_large matches 1 run scoreboard players add #egg_cmd stardew.animal.temp 4

# 根据品质调整 CMD
execute if score @s stardew.temp.quality matches 1 run scoreboard players add #egg_cmd stardew.animal.temp 1
execute if score @s stardew.temp.quality matches 2 run scoreboard players add #egg_cmd stardew.animal.temp 2
execute if score @s stardew.temp.quality matches 3 run scoreboard players add #egg_cmd stardew.animal.temp 3

# 确定视觉模型 CMD（普通鸡蛋 103，大鸡蛋 104）
scoreboard players set #visual_cmd stardew.animal.temp 103
execute if score @s stardew.temp.is_large matches 1 run scoreboard players set #visual_cmd stardew.animal.temp 104

# 在鸡舍内随机位置生成鸡蛋（相对鸡当前位置的±10格范围内）
# 这里使用建筑中心位置作为基准
# 首先检查鸡是否属于某个建筑

# 如果鸡有 building ID，则在建筑中心附近生成
execute if score @s stardew.animal.building matches 1.. run function stardew:animal/produce/spawn_egg_at_building

# 如果鸡没有建筑 ID，则在鸡当前位置附近生成（后备方案）
execute unless score @s stardew.animal.building matches 1.. at @s run function stardew:animal/produce/spawn_egg_at_position
