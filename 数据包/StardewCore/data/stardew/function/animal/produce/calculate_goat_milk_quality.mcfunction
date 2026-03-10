# ================================================================
# 星露谷物语 - 计算山羊羊奶品质
# ================================================================
# 用途：根据山羊的心情和友谊值计算羊奶品质
# @s = 山羊实体
# ================================================================

# 计算基础品质分数（心情 + 友谊）
scoreboard players operation #quality_score stardew.animal.temp = @s stardew.animal.mood
scoreboard players operation #quality_score stardew.animal.temp += @s stardew.animal.friendship

# 判断是否产大瓶羊奶（心情 >= 200 且友谊 >= 200）
execute if score @s stardew.animal.mood matches 200.. if score @s stardew.animal.friendship matches 200.. run tag @s add stardew.large_produce

# 根据品质分数设置产品 CMD
execute unless entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches ..299 run scoreboard players set @s stardew.animal.produce_cmd 8036
execute unless entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches 300..399 run scoreboard players set @s stardew.animal.produce_cmd 8037
execute unless entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches 400..499 run scoreboard players set @s stardew.animal.produce_cmd 8038
execute unless entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches 500.. run scoreboard players set @s stardew.animal.produce_cmd 8039

# 大瓶羊奶的品质
execute if entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches ..299 run scoreboard players set @s stardew.animal.produce_cmd 8040
execute if entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches 300..399 run scoreboard players set @s stardew.animal.produce_cmd 8041
execute if entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches 400..499 run scoreboard players set @s stardew.animal.produce_cmd 8042
execute if entity @s[tag=stardew.large_produce] if score #quality_score stardew.animal.temp matches 500.. run scoreboard players set @s stardew.animal.produce_cmd 8043

# 移除临时标记
tag @s remove stardew.large_produce

# 调试信息
#tellraw @a[tag=stardew.debug] ["",{"text":"[山羊羊奶品质] ","color":"gold"},{"text":"品质分数: ","color":"white"},{"score":{"name":"#quality_score","objective":"stardew.animal.temp"}},{"text":" CMD: ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.produce_item"}}]
