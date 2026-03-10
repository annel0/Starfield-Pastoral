# data/stardew/function/equipment/storage/init_player.mcfunction
# [执行者: 玩家] 初始化玩家装备数据 (如果还没有的话)

# 获取玩家UUID
data modify storage stardew:equipment temp_uuid set from entity @s UUID

# 检查是否已有数据 (通过scoreboard标记)
execute unless score @s sd_equip_boots matches 0.. run function stardew:equipment/storage/create_empty_data
