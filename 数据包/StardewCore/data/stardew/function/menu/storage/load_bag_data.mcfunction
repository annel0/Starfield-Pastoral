# data/stardew/function/menu/storage/load_bag_data.mcfunction
# [执行者: 箱子矿车] 加载背包数据

# 获取背包ID
execute store result score #BagID sd_storage_selected run scoreboard players get @p[scores={sd_storage_opened=1..}] sd_storage_selected

# 通过宏加载数据
function stardew:menu/storage/load_bag_data_macro with storage stardew:temp
