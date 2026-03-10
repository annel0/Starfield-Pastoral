# 初始化测试牛
# @s = 新生成的牛

# 分配ID
scoreboard players add #NextAnimalID stardew.animal.id 1
scoreboard players operation @s stardew.animal.id = #NextAnimalID stardew.animal.id

# 设置类型（201 = 牛）
scoreboard players set @s stardew.animal.type 201

# 设置初始数据
scoreboard players set @s stardew.animal.friendship 100
scoreboard players set @s stardew.animal.mood 150
execute unless entity @s[tag=spawn_as_adult] run scoreboard players set @s stardew.animal.age 0
execute if entity @s[tag=spawn_as_adult] run scoreboard players set @s stardew.animal.age 5
tag @s remove spawn_as_adult
scoreboard players set @s stardew.animal.friendship_today 0
scoreboard players set @s stardew.animal.fed_today 1
scoreboard players set @s stardew.animal.building 0

# 成年牛立即生成一次牛奶（用于测试）
execute if score @s stardew.animal.age matches 5.. run function stardew:animal/produce/check_single_cow

# 立即创建interaction实体
function stardew:animal/interact/create_interaction

# 生成视觉模型（必须在移除new标签之前调用）
function stardew:animal/visual/spawn_visual

# 移除new标签
tag @s remove stardew.animal.new

tellraw @a [{"text":"✓ 牛初始化完成 | ID: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"aqua"}]
