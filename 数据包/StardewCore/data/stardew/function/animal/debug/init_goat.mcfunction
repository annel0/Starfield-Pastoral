# ================================================================
# 星露谷物语 - 初始化山羊（调试用）
# ================================================================
# @s = 新生成的山羊
# ================================================================

# 分配唯一ID
scoreboard players add #next_animal_id stardew.animal.id 1
scoreboard players operation @s stardew.animal.id = #next_animal_id stardew.animal.id

# 设置类型为山羊（202）
scoreboard players set @s stardew.animal.type 202

# 设置基础属性
scoreboard players set @s stardew.animal.friendship 100
scoreboard players set @s stardew.animal.mood 200
scoreboard players set @s stardew.animal.health 3
scoreboard players set @s stardew.animal.produce_counter 0

# 调试信息
tellraw @a[tag=stardew.debug] ["",{"text":"[初始化山羊] ","color":"aqua"},{"text":"ID: ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 类型: 204","color":"gray"}]
