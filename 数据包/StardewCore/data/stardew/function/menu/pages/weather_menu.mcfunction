# data/stardew/function/menu/pages/weather_menu.mcfunction
# 天气子菜单 (Debug -> 天气)
# 执行者: 玩家 (@s)

# 1. 设置菜单层级为22（天气子菜单）
scoreboard players set @s sd_menu_level 22

# 1.3 立即设置点击冷却，防止同一次点击触发子菜单按钮
scoreboard players set @s sd_menu_click_cd 15

# 1.5 存储玩家编号用于后续选择器
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 1.8 清除所有 interaction 数据，防止残留点击事件
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 1.9 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 2. 配置5个天气按钮 (居中布局: slot 3-7，向右移一格)

# slot_3: 切换晴天 (CMD: 34)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":34}}}

# slot_4: 切换雨天 (CMD: 35)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":35}}}

# slot_5: 切换雷雨 (CMD: 36)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":36}}}

# slot_6: 切换下雪 (CMD: 37)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":37}}}

# slot_7: 返回 Debug菜单 (CMD: 14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# 3. 隐藏 slot_1, slot_2, slot_9 (CMD: 2)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 4. slot_8 保持为关闭菜单按钮 (CMD: 11) - 已在之前设置，这里不需要重复

# 5. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0
