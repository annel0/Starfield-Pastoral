# data/stardew/function/menu/pages/items_fish.mcfunction
# 鱼类子菜单 - 按季节/类型分类

# 0. 设置菜单层级
scoreboard players set @s sd_menu_level 12
scoreboard players set @s sd_menu_page 0

# 0.5 设置点击冷却
scoreboard players set @s sd_menu_click_cd 15

# 0.8 存储玩家编号并清除interaction数据
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 0.9 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 1. 更新菜单标题 (不超过5个字)
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {text:'{"text":"鱼类","color":"aqua","bold":true}'}

# 2. 更新按钮图标 (7个功能按钮)
# 按钮1: 春季鱼 (CMD:41990)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":41990}}}

# 按钮2: 夏季鱼 (CMD:42990)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":42990}}}

# 按钮3: 秋季鱼 (CMD:43990)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":43990}}}

# 按钮4: 冬季鱼 (CMD:44990)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":44990}}}

# 按钮5: 特殊鱼 (CMD:45000)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":45000}}}

# 按钮6: 垃圾 (CMD:40050)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":40050}}}

# 按钮7: 资源 (CMD:40120)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":40120}}}

# 按钮8: 返回物品主菜单 (CMD:14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# 按钮9: 关闭菜单 (CMD:11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 3. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.2
