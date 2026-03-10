# data/stardew/function/menu/pages/items_fish_special.mcfunction
# 特殊鱼类品质选择页面

# 0. 设置菜单层级
scoreboard players set @s sd_menu_level 125
scoreboard players set @s sd_menu_page 0

# 0.5 设置点击冷却
scoreboard players set @s sd_menu_click_cd 15

# 0.8 存储玩家编号并清除interaction数据
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 0.9 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 1. 更新菜单标题 (不超过5个字)
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {text:'{"text":"特殊鱼","color":"light_purple","bold":true}'}

# 2. 更新按钮图标 (4个品质按钮居中对齐在位置3-6)
# 按钮1-2: 空白
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 按钮3: 普通品质 (CMD:45000)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":45000}}}

# 按钮4: 银星品质 (CMD:45001)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":45001}}}

# 按钮5: 金星品质 (CMD:45002)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":45002}}}

# 按钮6: 钻石星品质 (CMD:45003)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":45003}}}

# 按钮7: 返回鱼类菜单 (CMD:14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# 按钮8: 关闭菜单 (CMD:11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 按钮9: 空白
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 3. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.2
