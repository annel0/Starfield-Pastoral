# 给自己一些测试食物
tellraw @s {"text":"[调试] 给予测试食物...","color":"yellow"}

# 作物测试
loot give @s loot stardew:items/crops/spring/parsnip_base
loot give @s loot stardew:items/crops/spring/strawberry_gold
loot give @s loot stardew:items/crops/summer/melon_diamond

# 鱼类测试
loot give @s loot stardew:items/fish/spring/anchovy_base
loot give @s loot stardew:items/fish/summer/tuna_silver
loot give @s loot stardew:items/fish/fall/salmon_gold

tellraw @s [{"text":"[调试] ","color":"yellow"},{"text":"已给予 6 种测试食物，右键食用！","color":"white"}]
tellraw @s [{"text":"[提示] ","color":"green"},{"text":"食物将恢复生命和能量（当前为默认值：生命+10，能量+15）","color":"gray"}]
