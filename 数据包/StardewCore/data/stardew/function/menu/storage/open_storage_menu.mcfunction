# data/stardew/function/menu/storage/open_storage_menu.mcfunction
# [执行者: 玩家] 打开存储一级子菜单

# 初始化存储页码（背包列表的翻页）
execute unless score @s sd_storage_page matches 0.. run scoreboard players set @s sd_storage_page 0

# 加载背包列表菜单
function stardew:menu/storage/pages/bags_list
