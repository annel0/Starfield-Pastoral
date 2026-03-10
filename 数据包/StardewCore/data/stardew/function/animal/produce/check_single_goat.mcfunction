# ================================================================
# 星露谷物语 - 检查单个山羊产羊奶
# ================================================================
# 用途：检查一只山羊是否可以产羊奶
# @s = 山羊实体
# ================================================================

# 山羊每2天产一次羊奶（与牛相同）
# produce_counter: 0 = 今天可以产奶, 1 = 还需1天

# 增加计数器
scoreboard players add @s stardew.animal.produce_counter 1

# 如果计数器达到2（已经过了2天），产羊奶
execute if score @s stardew.animal.produce_counter matches 2.. run function stardew:animal/produce/produce_goat_milk

# 调试信息
#tellraw @a[tag=stardew.debug] ["",{"text":"[山羊产奶检查] ","color":"aqua"},{"text":"山羊 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 计数器: ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.produce_counter"}},{"text":"/2","color":"gray"}]
