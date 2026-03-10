# ================================================================
# 星露谷物语 - 绵羊产羊毛检查
# ================================================================
# 用途：每天早上检查所有绵羊是否产羊毛
# 调用：从 time/new_day.mcfunction 调用

# 检查所有成年绵羊（type=203, age>=5）
execute as @e[type=sheep,tag=stardew.animal,scores={stardew.animal.type=203,stardew.animal.age=5..}] run function stardew:animal/produce/check_single_sheep
