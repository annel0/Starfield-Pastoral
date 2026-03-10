# data/stardew/function/menu/storage/api/create_bag.mcfunction
# [API] 为玩家创建一个新背包
# 使用方法: 以玩家身份执行此函数

# 增加背包数量
scoreboard players add @s sd_bag_count 1

# 获取新背包的ID（当前数量-1）
scoreboard players operation #NewBagID sd_storage_page = @s sd_bag_count
scoreboard players remove #NewBagID sd_storage_page 1

# 获取玩家UUID
data modify storage stardew:temp player_uuid set from entity @s UUID

# 初始化背包数据
execute store result storage stardew:temp bag_id int 1 run scoreboard players get #NewBagID sd_storage_page
function stardew:menu/storage/api/create_bag_macro with storage stardew:temp

tellraw @s {"text":"获得了新背包！","color":"green"}
