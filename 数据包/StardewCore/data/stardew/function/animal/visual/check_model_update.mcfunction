# ================================================================
# 星露谷物语 - 检查模型更新
# ================================================================
# 用途：检查动物年龄，必要时更新模型
# @s = 动物逻辑实体

# 只检查鸡（101）、鸭子（102）、兔子（103）、牛（201）、山羊（202）和绵羊（203）
execute unless score @s stardew.animal.type matches 101..103 unless score @s stardew.animal.type matches 201..203 run return 0

# 获取当前年龄
scoreboard players operation #check_age stardew.animal.temp = @s stardew.animal.age

# 找到视觉实体
scoreboard players operation #check_id stardew.animal.temp = @s stardew.animal.id

# ===== 鸡（101）=====
# 使用 Animated Java 模型，需要切换整个模型
# 幼年期（0-4天）→ 成年期（5天+）
execute if score @s stardew.animal.type matches 101 if score #check_age stardew.animal.temp matches 5.. run function stardew:animal/animated_java/check_chicken_growth

# ===== 鸭子（102）=====
# 使用 Animated Java 模型，需要切换整个模型
# 幼年期（0-4天）→ 成年期（5天+）
execute if score @s stardew.animal.type matches 102 if score #check_age stardew.animal.temp matches 5.. run function stardew:animal/animated_java/check_duck_growth

# ===== 兔子（103）=====
# 使用 Animated Java 模型，需要切换整个模型
# 幼年期（0-4天）→ 成年期（5天+）
execute if score @s stardew.animal.type matches 103 if score #check_age stardew.animal.temp matches 5.. run function stardew:animal/animated_java/check_rabbit_growth

# ===== 牛（201）=====
# 使用 Animated Java 模型，需要切换整个模型
# 幼年期（0-4天）→ 成年期（5天+）
execute if score @s stardew.animal.type matches 201 if score #check_age stardew.animal.temp matches 5.. run function stardew:animal/animated_java/check_cow_growth

# ===== 山羊（202）=====
# 使用 Animated Java 模型，需要切换整个模型
# 幼年期（0-4天）→ 成年期（5天+）
execute if score @s stardew.animal.type matches 202 if score #check_age stardew.animal.temp matches 5.. run function stardew:animal/animated_java/check_goat_growth

# ===== 绵羊（203）=====
# 使用 Animated Java 模型，需要切换整个模型
# 幼年期（0-4天）→ 成年期（5天+）
execute if score @s stardew.animal.type matches 203 if score #check_age stardew.animal.temp matches 5.. run function stardew:animal/animated_java/check_sheep_growth
