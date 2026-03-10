# data/stardew/function/menu/storage/hover/display_colored_text_macro.mcfunction
# 使用宏显示带颜色的文字
# 参数: $(color_name) $(current_bag_name)

$execute as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '["",{"nbt":"current_bag_name","storage":"stardew:temp","color":"$(color_name)","bold":true}]'
