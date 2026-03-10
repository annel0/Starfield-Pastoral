# data/stardew/function/menu/hover/highlight_off.mcfunction
# [执行者: 按钮] 关闭高光效果

# 1. 恢复到正常大小并关闭发光
data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.5f,0.5f,0.5f]},Glowing:0b}

# 2. 检查是否有其他按钮正在被瞄准(targeted=1) - 排除当前按钮
execute store result score #Temp sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num
execute store result score #CurrentSlot sd_menu_ctrl run scoreboard players get @s sd_menu_slot
scoreboard players set #OtherTargeted sd_menu_ctrl 0
execute as @e[tag=sd_menu_button,scores={sd_menu_targeted=1}] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl unless score @s sd_menu_slot = #CurrentSlot sd_menu_ctrl run scoreboard players add #OtherTargeted sd_menu_ctrl 1
execute as @e[tag=sd_menu_page_btn,scores={sd_menu_targeted=1}] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run scoreboard players add #OtherTargeted sd_menu_ctrl 1

# 3. 只有当没有其他按钮被瞄准时,才清空标题和描述文本
execute if score #OtherTargeted sd_menu_ctrl matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":""}'
execute if score #OtherTargeted sd_menu_ctrl matches 0 as @e[tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":""}'
