# data/stardew/function/menu/storage/buttons/back_to_list.mcfunction
# 返回到背包列表

# 清理重命名状态和书（无条件清理）
item replace entity @s weapon.offhand with air
clear @s writable_book
scoreboard players reset @s sd_storage_renaming

# 重置检测标记
scoreboard players set #AlreadyOpen sd_storage_temp 0

function stardew:menu/storage/pages/bags_list