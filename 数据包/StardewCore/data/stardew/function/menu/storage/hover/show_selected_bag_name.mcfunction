# data/stardew/function/menu/storage/hover/show_selected_bag_name.mcfunction
# 显示当前选中背包的名称
# [执行者: item_display按钮]

# 获取按钮的entity_num
execute store result score #Temp sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num

# 获取当前玩家的sd_storage_selected
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_storage_selected

# 计算显示编号（bag_id + 1）
execute store result score #TempBagID sd_storage_temp run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_storage_selected
scoreboard players add #TempBagID sd_storage_temp 1
execute store result storage stardew:temp macro.display_num int 1 run scoreboard players get #TempBagID sd_storage_temp

# 调用宏显示名称
function stardew:menu/storage/hover/display_bag_name_macro with storage stardew:temp macro
