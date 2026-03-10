# data/stardew/function/menu/pages/time_menu.mcfunction
# 时间调整菜单 (Debug菜单 -> 时间)
# 执行者: 玩家 (@s)

# 1. 设置菜单层级为24（时间调整菜单）
scoreboard players set @s sd_menu_level 24

# 1.3 立即设置点击冷却，防止同一次点击触发子菜单按钮
scoreboard players set @s sd_menu_click_cd 15

# 1.5 存储玩家编号用于后续选择器
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 1.8 清除所有 interaction 数据，防止残留点击事件
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 1.9 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 2. 配置8个按钮 (slot 2-9)

# slot_2: 快进一天 (CMD: 38)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":38}}}

# slot_3: 倒退一天 (CMD: 39)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":39}}}

# slot_4: 快进一季 (CMD: 40)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":40}}}

# slot_5: 倒退一季 (CMD: 41)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":41}}}

# slot_6: 快进一小时 (CMD: 42)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":42}}}

# slot_7: 倒退一小时 (CMD: 43)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":43}}}

# slot_8: 返回Debug菜单 (CMD: 14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# slot_9: 关闭菜单 (CMD: 11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 3. 隐藏slot_1 (CMD: 2)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 4. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0
