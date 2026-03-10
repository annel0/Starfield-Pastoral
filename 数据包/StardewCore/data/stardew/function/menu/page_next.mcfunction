# data/stardew/function/menu/page_next.mcfunction
# [执行者: 玩家] 翻到下一页

# 根据不同的菜单层级执行不同的翻页逻辑
# 物品菜单 (level=10): 只有2页 (0和1)
execute if score @s sd_menu_level matches 10 run scoreboard players add @s sd_menu_page 1
execute if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 2.. run scoreboard players set @s sd_menu_page 0
execute if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/pages/items_main
execute if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 1 run function stardew:menu/pages/items_main_p1

# 颜色选择菜单 (level=262): 3页 (0, 1, 2)
execute if score @s sd_menu_level matches 262 run scoreboard players add @s sd_menu_page 1
execute if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 3.. run scoreboard players set @s sd_menu_page 0
execute if score @s sd_menu_level matches 262 run function stardew:menu/storage/pages/color_menu

# 其他菜单的翻页逻辑 (如果有的话)
# execute if score @s sd_menu_level matches XX run ...
