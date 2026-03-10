# data/stardew/function/menu/storage/cleanup_cart.mcfunction
# 清理矿车和重命名状态

# 清理该玩家的矿车
execute as @e[type=chest_minecart,tag=sd_storage_cart] if score @s sd_cart_id = @p sd_menu_sequence run function stardew:menu/storage/save_and_kill_cart

# 清除矿车激活状态
scoreboard players set @s sd_storage_cart_active 0

# 清除打开的背包ID
scoreboard players reset @s sd_storage_opened

# 清除重命名状态
scoreboard players set @s sd_storage_renaming 0
