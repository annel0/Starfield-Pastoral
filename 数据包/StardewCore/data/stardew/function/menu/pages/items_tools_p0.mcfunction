# data/stardew/function/menu/pages/items_tools_p0.mcfunction
# 工具获取页面 - 第1页 (共3页,20个工具)

# 1. 更新菜单标题
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {text:'{"text":"🔧 工具 (1/3)","color":"gold","bold":true}'}

# 2. 更新按钮图标 (第1页:铜/铁/金/钻石斧头,铜/铁/金锄头)
# 按钮1: 铜斧头 (CMD:401)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":401}}}

# 按钮2: 铁斧头 (CMD:402)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":402}}}

# 按钮3: 金斧头 (CMD:403)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":403}}}

# 按钮4: 钻石斧头 (CMD:404)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":404}}}

# 按钮5: 铜锄头 (CMD:501)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":501}}}

# 按钮6: 铁锄头 (CMD:502)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":502}}}

# 按钮7: 金锄头 (CMD:503)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":503}}}

# 按钮8: 返回物品主菜单
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":12}}}

# 按钮9: 关闭菜单
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = @a[tag=sd_menu_opener,limit=1] sd_menu_sequence run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}
