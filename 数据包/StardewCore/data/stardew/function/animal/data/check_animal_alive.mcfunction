# 检查当前interaction对应的动物是否存活
# @s = interaction实体

# 标记是否找到对应动物
tag @s remove stardew.temp.found

# 寻找ID匹配的存活动物
execute as @e[tag=stardew.temp.alive] if score @s stardew.animal.id = @e[type=interaction,limit=1,sort=nearest] stardew.animal.id run tag @e[type=interaction,limit=1,sort=nearest] add stardew.temp.found

# 如果没找到，说明动物已死亡/消失，清理ID并删除interaction
execute unless entity @s[tag=stardew.temp.found] run scoreboard players reset @s stardew.animal.id
execute unless entity @s[tag=stardew.temp.found] run kill @s

# 清理标记
tag @s remove stardew.temp.found
