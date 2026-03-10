# data/stardew/function/menu/storage/buttons/color_prev.mcfunction
# 颜色选择上一页

scoreboard players remove @s sd_menu_page 1
execute if score @s sd_menu_page matches ..-1 run scoreboard players set @s sd_menu_page 2
function stardew:menu/storage/pages/color_menu
