# ================================================================
# 星露谷物语 - 测试鸭子产物视觉实体
# ================================================================
# 用途：强制生成鸭蛋和鸭毛实体用于测试视觉模型
# 调用：手动执行 /function stardew:animal/debug/test_duck_produce_visuals

# 测试鸭蛋视觉（CMD 106）
scoreboard players set #visual_cmd stardew.animal.temp 106
scoreboard players set #egg_cmd stardew.animal.temp 8008
tellraw @s [{"text":"[测试] ","color":"yellow"},{"text":"在你前方2格生成鸭蛋视觉实体（CMD 106）","color":"green"}]
execute positioned ^ ^ ^2 run function stardew:animal/produce/spawn_egg_at_position

# 测试鸭毛视觉（CMD 107）
scoreboard players set #visual_cmd stardew.animal.temp 107
scoreboard players set #egg_cmd stardew.animal.temp 8012
tellraw @s [{"text":"[测试] ","color":"yellow"},{"text":"在你前方4格生成鸭毛视觉实体（CMD 107）","color":"green"}]
execute positioned ^ ^ ^4 run function stardew:animal/produce/spawn_egg_at_position

tellraw @s [{"text":"[提示] ","color":"aqua"},{"text":"如果看不到鸭蛋/鸭毛，请检查资源包是否正确安装CMD 106和107的模型","color":"white"}]
