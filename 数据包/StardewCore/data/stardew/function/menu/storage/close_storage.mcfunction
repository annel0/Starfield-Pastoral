# data/stardew/function/menu/storage/close_storage.mcfunction
# [执行者: 玩家] 关闭存储系统（清理箱子矿车和数据）

# 如果有打开的箱子矿车，保存数据并清理
execute if score @s sd_storage_opened matches 1.. run function stardew:menu/storage/save_and_close_cart

# 清理重命名状态和书（无条件清理）
item replace entity @s weapon.offhand with air
clear @s writable_book
scoreboard players reset @s sd_storage_renaming

# 重置存储相关分数
scoreboard players reset @s sd_storage_page
scoreboard players reset @s sd_storage_opened
scoreboard players reset @s sd_storage_selected
