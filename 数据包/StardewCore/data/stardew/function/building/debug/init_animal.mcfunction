# ================================================================
# 星露谷物语 - 初始化测试动物 (Debug)
# ================================================================
# 用途：初始化新生成的测试动物的所有数据
# 调用：从spawn_chicken_in_building调用，作为动物执行

# 分配动物ID（使用现有系统）
execute store result score @s stardew.animal.id run scoreboard players add #NextAnimalID stardew.animal.id 1

# 设置动物类型（101=鸡）
scoreboard players set @s stardew.animal.type 101

# 找到最近的建筑并绑定
tag @e[type=marker,tag=stardew.building] remove stardew.temp.nearest_building
tag @e[type=marker,tag=stardew.building,distance=..20,limit=1,sort=nearest] add stardew.temp.nearest_building
scoreboard players operation @s stardew.animal.building_id = @e[type=marker,tag=stardew.temp.nearest_building,limit=1] stardew.building.id

# 设置初始数据
scoreboard players set @s stardew.animal.age 0
scoreboard players set @s stardew.animal.friendship 0
scoreboard players set @s stardew.animal.mood 150
scoreboard players set @s stardew.animal.fed_today 0
scoreboard players set @s stardew.animal.friendship_today 0

# 初始化状态
tag @s remove stardew.animal.is_outside
scoreboard players set @s stardew.animal.is_outside 0

# 设置无敌和持久化
data merge entity @s {Invulnerable:1b,PersistenceRequired:1b,Age:-999999}

# 创建交互实体 - 保留new标签，让manage_interactions在下一tick自动创建
# 这样确保动物实体完全初始化后再创建交互实体
# function stardew:animal/interact/create_interaction

# 显示消息
tellraw @a[distance=..20] [{"text":"[建筑] ","color":"gold"},{"text":"已生成测试鸡，动物ID: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":", 建筑ID: "},{"score":{"name":"@s","objective":"stardew.animal.building_id"}}]

# 保留new标签 - 让manage_interactions自动处理
# tag @s remove stardew.animal.new

# 清除临时标签
tag @e remove stardew.temp.nearest_building
