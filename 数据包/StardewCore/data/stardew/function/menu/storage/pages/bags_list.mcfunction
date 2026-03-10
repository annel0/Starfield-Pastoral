# data/stardew/function/menu/storage/pages/bags_list.mcfunction
# [执行者: 玩家] 显示背包列表（一级子菜单）

# 确保玩家数据已初始化
execute unless score @s sd_bag_count matches 1.. run function stardew:menu/storage/init_player_data

# 设置菜单层级为26（背包列表）
scoreboard players set @s sd_menu_level 26

# 保存并清理矿车（如果有的话）
execute if score @s sd_storage_cart_active matches 1 run function stardew:menu/storage/save_and_close_cart

# 清理重命名状态和残留的书与笔（注释掉，不要自动清理）
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

# 配置7个背包槽位（slot 1-7）
function stardew:menu/storage/pages/display_bag_slot_1
function stardew:menu/storage/pages/display_bag_slot_2
function stardew:menu/storage/pages/display_bag_slot_3
function stardew:menu/storage/pages/display_bag_slot_4
function stardew:menu/storage/pages/display_bag_slot_5
function stardew:menu/storage/pages/display_bag_slot_6
function stardew:menu/storage/pages/display_bag_slot_7

# Slot 8: 返回主菜单 (CMD: 14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# Slot 9: 关闭菜单 (CMD: 11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0

