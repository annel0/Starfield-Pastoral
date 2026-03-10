# 清理死亡动物的ID
# 找到所有已经不存在的动物ID对应的interaction并删除

# 为所有存活的动物打上临时标记
tag @e[type=#stardew:animals,tag=stardew.animal] add stardew.temp.alive

# 对于每个interaction，检查是否有对应的存活动物
execute as @e[type=interaction,tag=stardew.animal.interaction] at @s run function stardew:animal/data/check_animal_alive

# 移除临时标记
tag @e[tag=stardew.temp.alive] remove stardew.temp.alive
