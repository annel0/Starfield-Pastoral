# data/stardew/function/menu/storage/init_player_data.mcfunction
# 初始化玩家存储数据

# 使用scoreboard标记来确保只初始化一次全局storage
execute unless score #StorageInit sd_storage_temp matches 1 run function stardew:menu/storage/init_player_data_macro
execute unless score #StorageInit sd_storage_temp matches 1 run scoreboard players set #StorageInit sd_storage_temp 1

# 初始化为1个背包
scoreboard players set @s sd_bag_count 1
