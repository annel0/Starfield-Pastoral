# data/stardew/function/menu/storage/pages/color_page_0.mcfunction
# 颜色选择第1页 - 颜色0-6（默认+6种颜色）

# 设置菜单层级为262，页码为0
scoreboard players set @s sd_menu_level 262
scoreboard players set @s sd_menu_page 0

# 立即设置点击冷却
scoreboard players set @s sd_menu_click_cd 15

# 存储当前玩家序列号
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 清除所有 interaction 数据
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 重置按钮hover状态
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# Slot 1-7: 显示不同颜色的bundle（使用原版colored bundle）
# 颜色0 (默认bundle)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:bundle",count:1}}
# 颜色1 (白色)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:white_bundle",count:1}}
# 颜色2 (浅灰色)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:light_gray_bundle",count:1}}
# 颜色3 (灰色)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:gray_bundle",count:1}}
# 颜色4 (黑色)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:black_bundle",count:1}}
# 颜色5 (棕色)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:brown_bundle",count:1}}
# 颜色6 (红色)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:red_bundle",count:1}}

# Slot 8: 返回 (纸 CMD:14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# Slot 9: 关闭 (纸 CMD:11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}
summon interaction ~ ~ ~ {Tags:["sd_menu_display","sd_menu_interact","sd_color_next"],width:0.5f,height:0.5f}
execute rotated ~44 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_display","sd_menu_page_btn","sd_color_next"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":17}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.5f,0.5f,0.5f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute as @e[tag=sd_menu_display,tag=sd_color_next] at @s facing entity @e[tag=sd_menu_center,limit=1,sort=nearest] feet run tp @s ~ ~ ~ ~ ~
execute as @e[tag=sd_menu_display,tag=sd_color_next] store result score @s sd_menu_entity_num run scoreboard players get #CurrentPlayer sd_menu_ctrl
