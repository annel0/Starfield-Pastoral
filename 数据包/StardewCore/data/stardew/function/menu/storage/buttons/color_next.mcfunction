# data/stardew/function/menu/storage/buttons/color_next.mcfunction
# 颜色选择下一页

scoreboard players add @s sd_menu_page 1
execute if score @s sd_menu_page matches 3.. run scoreboard players set @s sd_menu_page 0
function stardew:menu/storage/pages/color_menu
