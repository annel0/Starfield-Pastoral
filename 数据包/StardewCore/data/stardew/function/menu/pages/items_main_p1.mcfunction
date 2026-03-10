# data/stardew/function/menu/pages/items_main_p1.mcfunction
# 物品主菜单 - 第1页 (Debug工具)

# 0. 设置菜单层级和页码
scoreboard players set @s sd_menu_level 10
scoreboard players set @s sd_menu_page 1

# 0.5 设置点击冷却，防止同一次点击触发子菜单按钮
scoreboard players set @s sd_menu_click_cd 15

# 0.8 存储玩家编号并清除interaction数据
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 0.9 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 1. 更新菜单标题 (不超过5个字)
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {text:'{"text":"物品","color":"gold","bold":true}'}

# 2. 更新按钮图标
# 按钮1: Debug工具 (CMD:9002 carrot_on_a_stick)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":9002}}}

# 按钮2: 矿物生成 (CMD:9105 carrot_on_a_stick)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":9105}}}

# 按钮3-7: 空白槽位 (CMD:2 透明)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 按钮8: 返回Debug菜单 (CMD:14 返回箭头)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# 按钮9: 关闭菜单 (CMD:11 关闭按钮)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 3. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.2


