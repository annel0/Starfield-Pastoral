# ================================================================
# 星露谷物语 - 检查并删除孤儿视觉实体
# ================================================================
# @s = 视觉实体

# 保存ID
scoreboard players operation #orphan_check stardew.animal.temp = @s stardew.animal.id

# 检查是否有对应的逻辑实体存在
execute store result score #found stardew.animal.temp if entity @e[type=#stardew:animals,tag=stardew.animal] if score @e[type=#stardew:animals,tag=stardew.animal] stardew.animal.id = #orphan_check stardew.animal.temp

# 如果没找到对应的逻辑实体，删除这个视觉实体
execute if score #found stardew.animal.temp matches 0 run kill @s
