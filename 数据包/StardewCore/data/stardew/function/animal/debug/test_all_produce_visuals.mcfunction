# ================================================================
# 星露谷物语 - 测试所有产物视觉实体
# ================================================================
# 用途：生成鸡蛋、鸭蛋、鸭毛的视觉实体进行对比测试
# 调用：手动执行 /function stardew:animal/debug/test_all_produce_visuals

tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gold"}]
tellraw @s [{"text":"[产物视觉测试] ","color":"yellow","bold":true},{"text":"在你周围生成所有产物类型","color":"white"}]
tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gold"}]

# 鸡蛋（CMD 103）
scoreboard players set #visual_cmd stardew.animal.temp 103
scoreboard players set #egg_cmd stardew.animal.temp 8000
tellraw @s [{"text":"→ ","color":"gray"},{"text":"前方2格: 普通鸡蛋 (CMD 103)","color":"white"}]
execute positioned ^ ^ ^2 run function stardew:animal/produce/spawn_egg_at_position

# 大鸡蛋（CMD 104）
scoreboard players set #visual_cmd stardew.animal.temp 104
scoreboard players set #egg_cmd stardew.animal.temp 8004
tellraw @s [{"text":"→ ","color":"gray"},{"text":"前方4格: 大鸡蛋 (CMD 104)","color":"white"}]
execute positioned ^ ^ ^4 run function stardew:animal/produce/spawn_egg_at_position

# 鸭蛋（CMD 106）
scoreboard players set #visual_cmd stardew.animal.temp 106
scoreboard players set #egg_cmd stardew.animal.temp 8008
tellraw @s [{"text":"→ ","color":"gray"},{"text":"左侧2格: 鸭蛋 (CMD 106)","color":"aqua"}]
execute positioned ^2 ^ ^ run function stardew:animal/produce/spawn_egg_at_position

# 鸭毛（CMD 107）
scoreboard players set #visual_cmd stardew.animal.temp 107
scoreboard players set #egg_cmd stardew.animal.temp 8012
tellraw @s [{"text":"→ ","color":"gray"},{"text":"右侧2格: 鸭毛 (CMD 107)","color":"aqua"}]
execute positioned ^-2 ^ ^ run function stardew:animal/produce/spawn_egg_at_position

tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gold"}]
tellraw @s [{"text":"[提示] ","color":"yellow"},{"text":"如果鸭蛋/鸭毛看不到，检查资源包的CMD 106/107模型","color":"gray"}]
