# stardew:museum/give_loop
# 循环给予物品
# macro参数: item_type, item_name

# 给予一个
$loot give @s loot stardew:items/$(item_type)s/$(item_name)

# 减少计数
scoreboard players remove @s sd_temp 1

# 如果还有剩余,继续循环
$execute if score @s sd_temp matches 1.. run function stardew:museum/give_loop {item_type:"$(item_type)",item_name:"$(item_name)"}