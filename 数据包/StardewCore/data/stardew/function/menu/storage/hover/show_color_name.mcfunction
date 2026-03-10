# data/stardew/function/menu/storage/hover/show_color_name.mcfunction
# 根据槽位和页码显示颜色名称
# [执行者: 按钮]

# 获取玩家和页码
execute store result score #Temp sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num
execute store result score #MenuPage sd_menu_ctrl run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_page

# 第0页 (颜色0-6)
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"原色","color":"white","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"白色","color":"white","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"浅灰色","color":"gray","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"灰色","color":"gray","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"黑色","color":"dark_gray","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"棕色","color":"gold","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"红色","color":"red","bold":true}'

# 第1页 (颜色7-13)
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"橙色","color":"gold","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"黄色","color":"yellow","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"黄绿色","color":"yellow","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"绿色","color":"green","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"青色","color":"dark_aqua","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"淡蓝色","color":"aqua","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"蓝色","color":"blue","bold":true}'

# 第2页 (颜色14-16)
execute if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"紫色","color":"dark_purple","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"品红色","color":"light_purple","bold":true}'
execute if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"粉色","color":"light_purple","bold":true}'
