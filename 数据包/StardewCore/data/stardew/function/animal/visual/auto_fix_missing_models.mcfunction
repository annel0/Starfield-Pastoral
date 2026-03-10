# ================================================================
# 自动修复缺失模型的动物
# ================================================================
# 用途：每5秒检查一次，自动为缺失模型的动物生成模型
# 调用：从 animal/core/tick.mcfunction 定期调用

# 检查所有鸡（type=101）
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 101 run function stardew:animal/visual/ensure_chicken_model

# 检查所有鸭（type=102）
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 102 run function stardew:animal/visual/ensure_duck_model

# 检查所有兔（type=103）
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 103 run function stardew:animal/visual/ensure_rabbit_model

# 检查所有牛（type=201）
execute as @e[type=cow,tag=stardew.animal] if score @s stardew.animal.type matches 201 run function stardew:animal/visual/ensure_cow_model

# 检查所有绵羊（type=203）
execute as @e[type=sheep,tag=stardew.animal] if score @s stardew.animal.type matches 203 run function stardew:animal/visual/ensure_sheep_model
