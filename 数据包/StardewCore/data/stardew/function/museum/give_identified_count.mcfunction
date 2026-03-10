# stardew:museum/give_identified_count
# 根据storage中的数量给予已鉴定物品
# macro参数: item_type, item_name

# 根据类型和数量调用loot table
execute store result score @s sd_temp run data get storage stardew:temp identify_count
$execute if score @s sd_temp matches 1.. run function stardew:museum/give_loop {item_type:"$(item_type)",item_name:"$(item_name)"}
