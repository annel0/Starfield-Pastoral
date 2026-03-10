# ================================================================
# 星露谷物语 - 兔子产物判定系统
# ================================================================
# 用途：每天早晨检查所有兔子，判定是否产物
# 调用：从 time/new_day.mcfunction 调用

# 遍历所有type=103的兔子
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 103 run function stardew:animal/produce/check_single_rabbit
