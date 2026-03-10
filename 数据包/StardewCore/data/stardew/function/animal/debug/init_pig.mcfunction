# ================================================================
# 星露谷物语 - 初始化猪数据
# ================================================================
# 用途：为新生成的猪设置基本属性和ID
# @s = 新生成的猪实体
# ================================================================

# 分配ID
scoreboard players add #NextAnimalID stardew.animal.id 1
scoreboard players operation @s stardew.animal.id = #NextAnimalID stardew.animal.id

# 设置动物类型 (猪 = 204)
scoreboard players set @s stardew.animal.type 204

# 初始化基本属性
scoreboard players set @s stardew.animal.friendship 100
scoreboard players set @s stardew.animal.mood 150
execute unless entity @s[tag=spawn_as_adult] run scoreboard players set @s stardew.animal.age 0
execute if entity @s[tag=spawn_as_adult] run scoreboard players set @s stardew.animal.age 10
tag @s remove spawn_as_adult
scoreboard players set @s stardew.animal.friendship_today 0
scoreboard players set @s stardew.animal.fed_today 1
scoreboard players set @s stardew.animal.building 0

# 设置购买价格（16,000g）
scoreboard players set @s stardew.animal.price 16000

# 初始化松露相关数据
scoreboard players set @s stardew.animal.has_produce 0
scoreboard players set @s stardew.animal.produce_counter 0

# 成年猪立即生成一次松露（用于测试）
execute if score @s stardew.animal.age matches 10.. run function stardew:animal/produce/check_single_pig

# 立即创建interaction实体
function stardew:animal/interact/create_interaction

# 生成视觉模型（必须在移除new标签之前调用）
function stardew:animal/visual/spawn_visual

# 移除new标签
tag @s remove stardew.animal.new

tellraw @a [{"text":"✓ 猪初始化完成 | ID: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"aqua"}]