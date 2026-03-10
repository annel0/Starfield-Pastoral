# data/stardew/function/menu/pages/equipment_recipes.mcfunction
# [执行者: 玩家] 打开设备配方子菜单

# 1. 设置菜单层级
scoreboard players set @s sd_menu_level 302
scoreboard players set @s sd_menu_page 0
scoreboard players set @s sd_menu_click_cd 15

# 2. 存储玩家编号
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 3. 清除interaction数据和重置hover
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 4. 显示所有配方（未解锁显示问号，已解锁显示实际图标）
# 配方201: 熔炉 (slot 1)
# 未解锁：CMD 15 (问号)
execute unless score @s stardew.recipe.201 matches 1.. as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":15}}}
# 已解锁：CMD 3001 (熔炉图标)
execute if score @s stardew.recipe.201 matches 1.. as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":3001}}}

# 配方202: 箱子 (slot 2)
# 未解锁：CMD 15 (问号)
execute unless score @s stardew.recipe.202 matches 1.. as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":15}}}
# 已解锁：CMD 3004 (箱子图标)
execute if score @s stardew.recipe.202 matches 1.. as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":3004}}}

# 配方203: 小桶 (slot 3)
# 未解锁：CMD 15 (问号)
execute unless score @s stardew.recipe.203 matches 1.. as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":15}}}
# 已解锁：CMD 3002 (小桶图标)
execute if score @s stardew.recipe.203 matches 1.. as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":3002}}}

# 其他槽位暂时清空
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 5. 返回和关闭按钮
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 6. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0

