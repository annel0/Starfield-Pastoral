# data/stardew/function/menu/storage/save_and_kill_cart.mcfunction
# 保存矿车内容并销毁

# 获取背包ID
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @p sd_storage_opened

# 保存物品到storage
function stardew:menu/storage/save_bag_macro with storage stardew:temp macro

# 销毁矿车
kill @s
