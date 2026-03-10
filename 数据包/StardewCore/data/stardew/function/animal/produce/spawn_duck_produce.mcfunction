# ================================================================
# 星露谷物语 - 生成鸭子产物实体
# ================================================================
# 用途：在鸭舍内生成鸭蛋或鸭毛实体
# 调用：从 check_single_duck.mcfunction 调用

# 根据 is_feather 和 quality 确定 CMD
# 鸭蛋：8008(base), 8009(silver), 8010(gold), 8011(diamond)
# 鸭毛：8012(base), 8013(silver), 8014(gold), 8015(diamond)

# 初始化 CMD 为 8008（鸭蛋基础品质）
scoreboard players set #egg_cmd stardew.animal.temp 8008

# 如果是鸭毛，CMD + 4
execute if score @s stardew.temp.is_feather matches 1 run scoreboard players add #egg_cmd stardew.animal.temp 4

# 根据品质调整 CMD
execute if score @s stardew.temp.quality matches 1 run scoreboard players add #egg_cmd stardew.animal.temp 1
execute if score @s stardew.temp.quality matches 2 run scoreboard players add #egg_cmd stardew.animal.temp 2
execute if score @s stardew.temp.quality matches 3 run scoreboard players add #egg_cmd stardew.animal.temp 3

# 确定视觉模型 CMD（鸭蛋 106，鸭毛 107）
scoreboard players set #visual_cmd stardew.animal.temp 106
execute if score @s stardew.temp.is_feather matches 1 run scoreboard players set #visual_cmd stardew.animal.temp 107

# 在鸭舍内随机位置生成产物（相对鸭当前位置的±10格范围内）
# 这里使用建筑中心位置作为基准
# 首先检查鸭是否属于某个建筑

# 如果鸭有 building ID，则在建筑中心附近生成
execute if score @s stardew.animal.building matches 1.. run function stardew:animal/produce/spawn_egg_at_building

# 如果鸭没有建筑 ID，则在鸭当前位置附近生成（后备方案）
execute unless score @s stardew.animal.building matches 1.. at @s run function stardew:animal/produce/spawn_egg_at_position
