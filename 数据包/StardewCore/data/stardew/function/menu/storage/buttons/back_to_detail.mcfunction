# data/stardew/function/menu/storage/buttons/back_to_detail.mcfunction
# 从颜色菜单返回详细菜单

# 保存并清理矿车（如果有的话）
execute if score @s sd_storage_cart_active matches 1 run function stardew:menu/storage/save_and_close_cart

function stardew:menu/storage/pages/bag_detail