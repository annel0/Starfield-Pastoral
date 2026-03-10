# ================================================================
# 星露谷物语 - 山羊产羊奶
# ================================================================
# 用途：山羊产出羊奶
# @s = 山羊实体
# ================================================================

# 重置计数器
scoreboard players set @s stardew.animal.produce_counter 0

# 设置产出标记（使用分数，与牛系统一致）
scoreboard players set @s stardew.animal.has_produce 1

# 计算羊奶品质
function stardew:animal/produce/calculate_goat_milk_quality

# 调试信息
tellraw @a[tag=stardew.debug] ["",{"text":"[山羊产奶] ","color":"green","bold":true},{"text":"山羊 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 产出了羊奶！","color":"yellow"}]
