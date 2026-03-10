# data/stardew/function/menu/storage/pages/display_bag_slot_1.mcfunction
# 显示背包槽位1

# 计算当前槽位对应的bag_id (从页开始)
scoreboard players operation #CheckBag sd_storage_temp = #StartBag sd_storage_page

# 调用宏函数设置物品
execute store result storage stardew:temp bag_id int 1 run scoreboard players get #CheckBag sd_storage_temp
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run function stardew:menu/storage/pages/set_bag_slot_macro with storage stardew:temp

