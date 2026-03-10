# data/stardew/function/equipment/display/hover_ring3.mcfunction
# [执行者: 玩家] 显示戒指3装备名称的hover文字
# 调用此函数时，已确认sd_equip_ring3 matches 1..

execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 从storage读取装备显示名称
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data modify entity @s text set from storage stardew:equipment ring3.display_name
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {text:'{"text":"按住 Shift+右键取下","color":"yellow"}'}
