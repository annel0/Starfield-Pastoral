# data/stardew/function/menu/storage/pages/bag_detail.mcfunction
# [执行者: 玩家] 显示背包详细操作菜单（二级子菜单）

# 设置菜单层级为261（背包详情）
scoreboard players set @s sd_menu_level 261

# 清理重命名状态和残留的书与笔（注释掉，不要自动清理，让玩家完成重命名或手动取消）
# execute if score @s sd_storage_renaming matches 1 run clear @s writable_book
# scoreboard players reset @s sd_storage_renaming

# 立即设置点击冷却，防止同一次点击触发子菜单按钮
scoreboard players set @s sd_menu_click_cd 15

# 存储当前玩家序列号
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 清除所有 interaction 数据，防止残留点击事件
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 重置所有按钮的hover状态
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 获取玩家背包数量供宏使用
execute store result score #PlayerBagCount sd_storage_temp run scoreboard players get @s sd_bag_count

# Slot 1: 显示当前背包图标
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @s sd_storage_selected
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run function stardew:menu/storage/pages/set_bag_slot_macro with storage stardew:temp macro

# Slot 2: 隐藏 (CMD: 2)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# Slot 3: 打开背包 (纸 CMD:60)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":60}}}

# Slot 4: 重命名 (书与笔)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:writable_book",count:1}}

# Slot 5: 换色 (纸 CMD:61)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":61}}}

# Slot 6: 返回 (纸 CMD:14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# Slot 7: 关闭 (纸 CMD:11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# Slot 8-9: 隐藏
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0
