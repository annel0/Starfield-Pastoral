# data/stardew/function/menu/storage/load_player_data.mcfunction
# 加载玩家存储数据

# 获取玩家UUID
data modify storage stardew:temp player_uuid set from entity @s UUID

# 检查玩家是否已有存储数据（简化版）
# 注意：由于NBT路径限制，这里使用简化的检查方式
execute store result score @s sd_bag_count run data get storage stardew:storage bag_count

# 如果背包数量为0，初始化为默认值
execute if score @s sd_bag_count matches 0 run scoreboard players set @s sd_bag_count 3