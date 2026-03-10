# ================================================================
# 星露谷物语 - 检查猪产松露
# ================================================================
# 用途：每天检查是否有猪可以产松露
# 执行者：系统（在 new_day 中调用）
# ================================================================

# 检查所有成年猪（年龄 >= 10，type=204）
execute as @e[type=pig,tag=stardew.animal,scores={stardew.animal.type=204,stardew.animal.age=10..}] run function stardew:animal/produce/check_single_pig