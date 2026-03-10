# 准备主手食物数据（在消费前存储）

# 只有在没有标记时才存储（避免重复）
execute unless entity @s[tag=sd_food_stored] run function stardew:food/store_mainhand

# 标记已存储
tag @s add sd_food_stored
