# data/stardew/function/menu/storage/tick_cart.mcfunction
# [每tick执行] 让箱子矿车跟随玩家

# 对所有有激活箱子矿车的玩家执行
execute as @a[scores={sd_storage_cart_active=1..}] at @s run function stardew:menu/storage/update_cart_position

# 检测玩家是否离开了背包详细菜单（level=261）
execute as @a[scores={sd_storage_cart_active=1..}] unless score @s sd_menu_level matches 261 run function stardew:menu/storage/save_and_close_cart
