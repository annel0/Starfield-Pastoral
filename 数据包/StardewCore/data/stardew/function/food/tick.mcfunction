# 每 tick 检测玩家是否正在使用食物
# 在物品被消费前就存储数据

# 检测主手正在使用且即将消费的食物
execute as @a if items entity @s weapon.mainhand *[custom_data~{is_food:1b},consumable] if predicate stardew:is_using_item run function stardew:food/prepare_mainhand

# 检测副手正在使用且即将消费的食物
execute as @a if items entity @s weapon.offhand *[custom_data~{is_food:1b},consumable] if predicate stardew:is_using_item run function stardew:food/prepare_offhand
