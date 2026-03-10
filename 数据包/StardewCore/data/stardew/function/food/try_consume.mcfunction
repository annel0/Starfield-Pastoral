# 尝试消耗食物
# 由 check.mcfunction 调用

# 检查冷却时间
execute if score @s sd_food_cooldown matches 1.. run scoreboard players reset @s sd_use_item
execute if score @s sd_food_cooldown matches 1.. run return 0

# 阻止宝石被右键使用(宝石需要在博物馆鉴定,不能直接使用)
execute if items entity @s weapon.mainhand paper[custom_data~{item_type:"gem"}] run scoreboard players reset @s sd_use_item
execute if items entity @s weapon.mainhand paper[custom_data~{item_type:"gem"}] run return 0
execute if items entity @s weapon.offhand paper[custom_data~{item_type:"gem"}] run scoreboard players reset @s sd_use_item
execute if items entity @s weapon.offhand paper[custom_data~{item_type:"gem"}] run return 0

# 检查主手是否为可食用物品
execute if items entity @s weapon.mainhand *[custom_data~{is_food:1}] run function stardew:food/consume_mainhand

# 检查副手是否为可食用物品
execute if items entity @s weapon.offhand *[custom_data~{is_food:1}] run function stardew:food/consume_offhand

# 重置计分板
scoreboard players reset @s sd_use_item
