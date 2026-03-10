# data/stardew/function/menu/close.mcfunction
# [执行者: 玩家] 关闭菜单UI

# 1. 获取玩家编号
execute store result score #TempClose sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 2. 杀死所有属于该玩家的UI实体
execute as @e[tag=sd_menu_display] if score @s sd_menu_entity_num = #TempClose sd_menu_ctrl run kill @s

# 2.5 清理可能残留的射线marker
kill @e[tag=sd_menu_ray]

# 3. 重置玩家序列号
scoreboard players reset @s sd_menu_sequence
scoreboard players reset @s sd_menu_page
scoreboard players reset @s sd_menu_state
scoreboard players reset @s sd_menu_level

# 3.5 清理存储系统状态
execute if score @s sd_storage_cart_active matches 1.. run function stardew:menu/storage/save_and_close_cart

# 清理重命名的书（在重置分数前）
item replace entity @s weapon.offhand with air
clear @s writable_book

scoreboard players reset @s sd_storage_page
scoreboard players reset @s sd_storage_selected
scoreboard players reset @s sd_storage_opened
scoreboard players reset @s sd_storage_opened
scoreboard players reset @s sd_storage_selected
scoreboard players reset @s sd_storage_renaming
scoreboard players reset @s sd_color_page
scoreboard players reset @s sd_storage_cart_active

# 4. 播放音效
playsound block.ender_chest.close player @s ~ ~ ~ 1 1.2
