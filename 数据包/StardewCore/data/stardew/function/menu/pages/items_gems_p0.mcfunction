# data/stardew/function/menu/pages/items_gems_p0.mcfunction
# 宝石获取页面 - 第1页 (共2页,14个宝石)

# 1. 更新菜单标题
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {text:'{"text":"💎 宝石 (1/2)","color":"light_purple","bold":true}'}

# 2. 更新按钮图标 (第1页:7个宝石)
# 按钮1: 紫水晶 (CMD:30101)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30101}}}

# 按钮2: 海蓝宝石 (CMD:30102)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30102}}}

# 按钮3: 钻石 (CMD:30103)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30103}}}

# 按钮4: 绿宝石 (CMD:30104)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30104}}}

# 按钮5: 翡翠 (CMD:30105)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30105}}}

# 按钮6: 火水晶 (CMD:30106)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30106}}}

# 按钮7: 冰晶 (CMD:30107)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":30107}}}

# 按钮8: 返回物品主菜单
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":12}}}

# 按钮9: 关闭菜单
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}
