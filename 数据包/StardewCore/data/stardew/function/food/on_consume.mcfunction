# 食用物品后触发的函数
# 由 advancement 触发（此时物品已被消耗）

# 撤销 advancement 以便下次触发
advancement revoke @s only stardew:food/consume_food

# 移除存储标记（允许下次存储）
tag @s remove sd_food_stored

# 检查是否有预存储的食物数据
execute if data storage stardew:temp pending_food.food_health run function stardew:food/restore with storage stardew:temp pending_food

# 如果没有预存储数据，尝试从手中读取（堆叠情况）
execute unless data storage stardew:temp pending_food.food_health store success score #found_main sd_temp if items entity @s weapon.mainhand *[custom_data~{is_food:1b}] run function stardew:food/process_mainhand
execute unless data storage stardew:temp pending_food.food_health if score #found_main sd_temp matches 0 store success score #found_off sd_temp if items entity @s weapon.offhand *[custom_data~{is_food:1b}] run function stardew:food/process_offhand

# 如果还是没找到，使用默认值
execute unless data storage stardew:temp pending_food.food_health if score #found_main sd_temp matches 0 if score #found_off sd_temp matches 0 run function stardew:food/restore_default

# 清理存储数据
data remove storage stardew:temp pending_food
scoreboard players reset #found_main sd_temp
scoreboard players reset #found_off sd_temp
