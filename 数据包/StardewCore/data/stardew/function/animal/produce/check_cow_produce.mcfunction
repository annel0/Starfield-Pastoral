# ================================================================
# 星露谷物语 - 牛产奶判定系统
# ================================================================
# 用途：每天早上判定牛是否产奶
# 调用：从 time/new_day.mcfunction 调用

# 只处理牛（type=201）且已成熟（age>=5）的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 201 if score @s stardew.animal.age matches 5.. run function stardew:animal/produce/check_single_cow
