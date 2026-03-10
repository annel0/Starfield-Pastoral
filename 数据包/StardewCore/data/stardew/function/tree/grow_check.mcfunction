# data/stardew/functions/tree/grow_check.mcfunction
# [执行者: 树木交互实体]

# 树木通常四季都能生长，或者你可以像作物一样加季节限制
# 这里默认全季节生长

# 1. 增加年龄
scoreboard players add @s sd_crop_age 1

# 2. 刷新视觉 (根据树木类型调用对应的visual函数)
execute if entity @s[tag=tree_oak] run function stardew:tree/visual/oak
execute if entity @s[tag=tree_maple] run function stardew:tree/visual/maple
execute if entity @s[tag=tree_pine] run function stardew:tree/visual/pine
execute if entity @s[tag=tree_mahogany] run function stardew:tree/visual/mahogany