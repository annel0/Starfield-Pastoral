# ================================================================
# 星露谷物语 - 鸡产蛋判定系统
# ================================================================
# 用途：每天早上判定鸡是否产蛋
# 调用：从 time/new_day.mcfunction 调用

# 只处理鸡（type=101）且已成熟（age>=3）的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 101 if score @s stardew.animal.age matches 3.. run function stardew:animal/produce/check_single_chicken
