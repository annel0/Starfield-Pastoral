# ================================================================
# 星露谷物语 - 生成动物视觉模型
# ================================================================
# 用途：为动物召唤 Animated Java 模型
# 调用：从生成动物时调用，作为新动物执行
# @s = 刚生成的动物逻辑实体

# 获取动物类型和年龄
scoreboard players operation #animal_type stardew.animal.temp = @s stardew.animal.type
scoreboard players operation #animal_age stardew.animal.temp = @s stardew.animal.age

# ================================================================
# 鸡（101）- 使用 Animated Java 模型
# ================================================================
# 幼年鸡（0-4天）
execute if score #animal_type stardew.animal.temp matches 101 if score #animal_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_chicken_baby
# 成年鸡（5天+）
execute if score #animal_type stardew.animal.temp matches 101 if score #animal_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_chicken

# ================================================================
# 鸭子（102）- 使用 Animated Java 模型
# ================================================================
# 幼年鸭（0-4天）- 使用 chicken_baby 模型
execute if score #animal_type stardew.animal.temp matches 102 if score #animal_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_duck_baby
# 成年鸭（5天+）
execute if score #animal_type stardew.animal.temp matches 102 if score #animal_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_duck

# ================================================================
# 兔子（103）- 使用 Animated Java 模型
# ================================================================
# 幼年兔（0-4天）- 使用 rabbit_baby 模型
execute if score #animal_type stardew.animal.temp matches 103 if score #animal_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_rabbit_baby
# 成年兔（5天+）
execute if score #animal_type stardew.animal.temp matches 103 if score #animal_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_rabbit

# ================================================================
# 牛（201）- 使用 Animated Java 模型
# ================================================================
# 幼年牛（0-4天）- 使用 cow_baby 模型
execute if score #animal_type stardew.animal.temp matches 201 if score #animal_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_cow_baby
# 成年牛（5天+）
execute if score #animal_type stardew.animal.temp matches 201 if score #animal_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_cow

# ================================================================
# 绵羊（203）- 使用 Animated Java 模型
# ================================================================
# 幼年绵羊（0-4天）- 使用 sheep_baby 模型
execute if score #animal_type stardew.animal.temp matches 203 if score #animal_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_sheep_baby
# 成年绵羊（5天+）
execute if score #animal_type stardew.animal.temp matches 203 if score #animal_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_sheep

# ================================================================
# 山羊（202）- 使用 Animated Java 模型
# ================================================================
# 幼年山羊（0-4天）- 使用 goat_baby 模型
execute if score #animal_type stardew.animal.temp matches 202 if score #animal_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_goat_baby
# 成年山羊（5天+）
execute if score #animal_type stardew.animal.temp matches 202 if score #animal_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_goat

# ================================================================
# 猪（204）- 使用 Animated Java 模型
# ================================================================
# 幼年猪（0-9天）
execute if score #animal_type stardew.animal.temp matches 204 if score #animal_age stardew.animal.temp matches ..9 at @s run function stardew:animal/animated_java/summon_pig_baby
# 成年猪（10天+）
execute if score #animal_type stardew.animal.temp matches 204 if score #animal_age stardew.animal.temp matches 10.. at @s run function stardew:animal/animated_java/summon_pig

# ================================================================
# 召唤模型（旧系统，鸡已改用 AJ）
# ================================================================
# execute if score #spawn_model stardew.animal.temp matches 1 at @s run function stardew:animal/visual/summon_model
