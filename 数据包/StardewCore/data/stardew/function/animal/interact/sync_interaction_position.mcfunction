# ================================================================
# 星露谷物语 - 同步交互实体位置
# ================================================================
# 用途：将interaction实体传送到对应的动物位置
# @s = interaction实体
# ================================================================

# 找到ID匹配的动物并传送到它的位置
# 使用tag标记来确保精确匹配
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = @e[type=interaction,tag=stardew.animal.interaction,limit=1,sort=nearest] stardew.animal.id run tag @s add stardew.temp.target_animal

# 传送到目标动物位置
execute if entity @e[tag=stardew.temp.target_animal] run tp @s @e[tag=stardew.temp.target_animal,limit=1]

# 如果找不到对应的动物（动物死亡或消失），删除interaction并清理ID
execute unless entity @e[tag=stardew.temp.target_animal] run scoreboard players reset @s stardew.animal.id
execute unless entity @e[tag=stardew.temp.target_animal] run kill @s

# 清除临时标记
tag @e[tag=stardew.temp.target_animal] remove stardew.temp.target_animal
