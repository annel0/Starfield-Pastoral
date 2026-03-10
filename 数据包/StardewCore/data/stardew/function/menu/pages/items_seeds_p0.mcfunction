# data/stardew/function/menu/pages/items_seeds_p0.mcfunction
# 种子获取页面 - 第1页 (共5页,38个种子)

# 1. 更新菜单标题
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {text:'{"text":"🌱 种子 (1/5)","color":"dark_green","bold":true}'}

# 2. 更新按钮图标 (使用种子的CMD)
# 按钮1: 苋菜种子 (CMD:2304)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2304}}}

# 按钮2: 远古种子 (CMD:需要查找)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2305}}}

# 按钮3: 洋蓟种子
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2306}}}

# 按钮4: 蓝莓种子
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2307}}}

# 按钮5: 蓝爵士种子
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2308}}}

# 按钮6: 白菜种子
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2309}}}

# 按钮7: 花椰菜种子
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":2310}}}

# 按钮8: 返回物品主菜单
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":12}}}

# 按钮9: 关闭菜单
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}
