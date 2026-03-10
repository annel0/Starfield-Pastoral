# data/stardew/function/menu/pages/debug_main.mcfunction
# 加载Debug菜单第一页 - 作弊模式主菜单
# 执行者: 玩家 (@s)
# 前置: 玩家拥有sd_debug标签

# 1. 设置菜单层级
scoreboard players set @s sd_menu_level 1

# 1.5 设置点击冷却，防止同一次点击触发子菜单按钮
scoreboard players set @s sd_menu_click_cd 15

# 1.8 存储玩家编号并清除interaction数据
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 1.9 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 2. 更改按钮CMD (只改中间6个按钮,其他保持不变)
# 位置布局: slot_2~slot_7 居中对齐显示6个按钮
# slot_1 和 slot_8/9 留空

# slot_2: 季节 (CMD: 20)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":20}}}

# slot_3: 天气 (CMD: 21)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":21}}}

# slot_4: 时间 (CMD: 22)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":22}}}

# slot_5: 物品 (CMD: 23)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":23}}}

# slot_6: 升级 (CMD: 24)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":24}}}

# slot_7: 返回 (CMD: 14 - 原关闭按钮图标复用)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# slot_8: 关闭菜单 (CMD: 11 - 原关闭图标)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# slot_1: 隐藏 (设为透明/空)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# slot_9: 隐藏
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 3. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.2
