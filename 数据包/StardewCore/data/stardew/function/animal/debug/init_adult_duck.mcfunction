# 初始化成年测试鸭子
# @s = 新生成的动物

# 分配ID
scoreboard players add #NextAnimalID stardew.animal.id 1
scoreboard players operation @s stardew.animal.id = #NextAnimalID stardew.animal.id

# 设置类型（102 = 鸭子）
scoreboard players set @s stardew.animal.type 102

# 设置初始数据（成年鸭子）
scoreboard players set @s stardew.animal.friendship 100
scoreboard players set @s stardew.animal.mood 150
scoreboard players set @s stardew.animal.age 6
scoreboard players set @s stardew.animal.friendship_today 0
scoreboard players set @s stardew.animal.fed_today 0
scoreboard players set @s stardew.animal.building 0

# 立即创建interaction实体
function stardew:animal/interact/create_interaction

# 生成视觉模型（必须在移除new标签之前调用）
function stardew:animal/visual/spawn_visual

# 移除new标签
tag @s remove stardew.animal.new

tellraw @a [{"text":"✓ 成年鸭子初始化完成 | ID: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"aqua"}]
