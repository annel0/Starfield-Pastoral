# ================================================================
# 星露谷物语 - 检查山羊产羊奶
# ================================================================
# 用途：每天检查是否有山羊可以产羊奶
# 执行者：系统（在 new_day 中调用）
# ================================================================

# 检查所有成年山羊（年龄 >= 5，type=202）
# 注意：山羊使用sheep实体，通过type=202区分
execute as @e[type=sheep,tag=stardew.animal,scores={stardew.animal.type=202,stardew.animal.age=5..}] run function stardew:animal/produce/check_single_goat
