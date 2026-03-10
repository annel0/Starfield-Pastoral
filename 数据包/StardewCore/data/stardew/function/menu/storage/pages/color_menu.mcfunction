# data/stardew/function/menu/storage/pages/color_menu.mcfunction
# 颜色选择菜单 - 统一入口，使用sd_menu_page区分页面

# 设置菜单层级为262（颜色选择）
scoreboard players set @s sd_menu_level 262

# 初始化颜色页码（如果未设置）
execute unless score @s sd_menu_page matches 0..2 run scoreboard players set @s sd_menu_page 0

# 立即设置点击冷却
scoreboard players set @s sd_menu_click_cd 15

# 存储当前玩家序列号
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 清除所有 interaction 数据
execute as @e[type=interaction,tag=sd_menu_interact] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data remove entity @s interaction

# 重置按钮hover状态
execute as @e[type=item_display,tag=sd_menu_button] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run scoreboard players set @s sd_menu_targeted_prev 0

# 根据页码加载不同的颜色
execute if score @s sd_menu_page matches 0 run function stardew:menu/storage/pages/load_color_page_0
execute if score @s sd_menu_page matches 1 run function stardew:menu/storage/pages/load_color_page_1
execute if score @s sd_menu_page matches 2 run function stardew:menu/storage/pages/load_color_page_2

# Slot 8: 返回背包详情 (CMD: 14)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_8] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":14}}}

# Slot 9: 关闭菜单 (CMD: 11)
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_9] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":11}}}

# 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.0
