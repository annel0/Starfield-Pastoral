# ================================================================
# 星露谷物语 - 鸭子产物判定系统
# ================================================================
# 用途：每天早上判定鸭子是否产物（鸭蛋或鸭毛）
# 调用：从 time/new_day.mcfunction 调用

# 只处理鸭子（type=102）且已成熟（age>=5）的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 102 if score @s stardew.animal.age matches 5.. run function stardew:animal/produce/check_single_duck
