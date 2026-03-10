# 消耗副手食物
# 读取食物数据并恢复生命/能量

# 存储物品数据到临时存储
item replace block 0 -64 0 container.0 from entity @s weapon.offhand
data modify storage stardew:temp food_data set from block 0 -64 0 Items[0].components."minecraft:custom_data"

# 检查是否真的是可食用物品
execute unless data storage stardew:temp food_data.is_food run return 0

# 读取恢复值
execute store result score #food_health sd_temp run data get storage stardew:temp food_data.food_health
execute store result score #food_energy sd_temp run data get storage stardew:temp food_data.food_energy

# 恢复生命和能量
function stardew:food/restore with storage stardew:temp food_data

# 减少物品数量（副手）
item modify entity @s weapon.offhand stardew:food/consume_one

# 播放音效和粒子
playsound minecraft:entity.generic.eat player @a ~ ~ ~ 1 1
particle minecraft:item{item:"minecraft:bread"} ~ ~1.5 ~ 0.3 0.3 0.3 0.1 10

# 设置冷却时间（10 tick = 0.5秒）
scoreboard players set @s sd_food_cooldown 10

# 清理临时数据
data remove storage stardew:temp food_data
