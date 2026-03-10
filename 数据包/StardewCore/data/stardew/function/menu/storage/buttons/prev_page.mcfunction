# data/stardew/function/menu/storage/buttons/prev_page.mcfunction
# 上一页

scoreboard players remove @s sd_storage_page 1
execute if score @s sd_storage_page matches ..-1 run scoreboard players set @s sd_storage_page 0
function stardew:menu/storage/pages/bags_list
