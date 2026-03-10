# data/stardew/function/menu/pages/equipment_menu.mcfunction
# 装备菜单 - 显示装备槽位
# 执行者: 玩家 (@s)

# 1. 初始化玩家装备数据 (如果还没有)
function stardew:equipment/storage/init_player

# 2. 设置菜单层级为2（装备菜单）
scoreboard players set @s sd_menu_level 2

# 3. 立即设置点击冷却，防止同一次点击触发子菜单按钮
scoreboard players set @s sd_menu_click_cd 15

# 4. 存储玩家编号用于后续选择器
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 5. 清除所有 interaction 数据，防止残留点击事件
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 6. 重置所有按钮的hover状态,确保切换页面后hover文字能正确更新
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# ===== 配置槽位按钮 =====

# slot 1: 鞋子槽位 (空=CMD:50, 已装备=显示装备)
execute if score @s sd_equip_boots matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_1] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":50}}}
execute if score @s sd_equip_boots matches 1 run function stardew:equipment/display/show_boots

# slot 2: 戒指槽位1 (空=CMD:51, 已装备=显示装备)
execute if score @s sd_equip_ring1 matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":51}}}
execute if score @s sd_equip_ring1 matches 1 run function stardew:equipment/display/show_ring1

# slot 3: 戒指槽位2 (空=CMD:51, 已装备=显示装备)
execute if score @s sd_equip_ring2 matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_3] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":51}}}
execute if score @s sd_equip_ring2 matches 1 run function stardew:equipment/display/show_ring2

# slot 4: 戒指槽位3 (锁定=CMD:16, 解锁空=CMD:51, 已装备=显示装备)
execute if score @s sd_unlock_ring3 matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":16}}}
execute if score @s sd_unlock_ring3 matches 1 if score @s sd_equip_ring3 matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_4] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":51}}}
execute if score @s sd_unlock_ring3 matches 1 if score @s sd_equip_ring3 matches 1 run function stardew:equipment/display/show_ring3

# slot 5: 戒指槽位4 (锁定=CMD:16, 解锁空=CMD:51, 已装备=显示装备)
execute if score @s sd_unlock_ring4 matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":16}}}
execute if score @s sd_unlock_ring4 matches 1 if score @s sd_equip_ring4 matches 0 as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_5] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":51}}}
execute if score @s sd_unlock_ring4 matches 1 if score @s sd_equip_ring4 matches 1 run function stardew:equipment/display/show_ring4

# ===== 隐藏不使用的槽位 =====
# slot 6-7: 隐藏 (CMD:2)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_6] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_7] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# ===== 功能按钮 =====
# slot 8: 返回主菜单 (CMD:14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# slot 9: 关闭菜单 (CMD:11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# ===== 设置菜单标题 =====
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {text:'{"text":"装备栏","color":"light_purple","bold":true}'}

# ===== 清空描述文本 (将在hover时显示) =====
execute as @e[type=text_display,tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {text:'{"text":""}'}

# 7. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0
