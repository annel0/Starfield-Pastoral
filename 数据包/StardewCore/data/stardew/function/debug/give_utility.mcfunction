# data/stardew/function/debug/give_utility.mcfunction
# 给予所有实用设施物品用于测试

tellraw @s {"text":"===== 实用设施物品 =====","color":"gold","bold":true}

# 熔炉
loot give @s loot stardew:items/utility/furnace

# 小桶
loot give @s loot stardew:items/utility/keg

# 树液提取器
loot give @s loot stardew:items/utility/tapper

# 箱子
loot give @s loot stardew:items/utility/chest

tellraw @s {"text":"✓ 已获得所有实用设施物品！","color":"green"}
