# data/stardew/function/menu/storage/save_bag_data.mcfunction
# [执行者: 箱子矿车] 保存背包数据

# 获取背包ID从最近的玩家
execute store result storage stardew:temp macro.bag_id int 1 run scoreboard players get @p[scores={sd_storage_cart_active=1}] sd_storage_selected

# 保存物品数据到storage
function stardew:menu/storage/save_bag_data_macro with storage stardew:temp macro
