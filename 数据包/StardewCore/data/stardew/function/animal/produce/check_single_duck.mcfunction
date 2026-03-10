# ================================================================
# 星露谷物语 - 单只鸭子产物判定
# ================================================================
# 用途：判定一只鸭子是否产物（每2天产一次）
# 调用：从 check_duck_produce.mcfunction 调用

# 初始化产物标记为0（不产）
scoreboard players set @s stardew.temp.produce 0

# 检查是否喂食（未喂食则不产物）
execute unless score @s stardew.animal.fed_today matches 1.. run scoreboard players set @s stardew.animal.fed_today 1

# 鸭子每2天产一次，检查 age % 2 == 0
scoreboard players operation #duck_cycle stardew.animal.temp = @s stardew.animal.age
scoreboard players operation #duck_cycle stardew.animal.temp %= #2 stardew.animal.temp

# 如果是产物日（age能被2整除）
execute if score #duck_cycle stardew.animal.temp matches 0 run scoreboard players set @s stardew.temp.produce 1

# 如果Mood < 70，进行概率判定（与鸡相同的逻辑）
execute if score @s stardew.temp.produce matches 1 if score @s stardew.animal.mood matches ..69 store result score #rand stardew.animal.temp run random value 0..70
execute if score @s stardew.temp.produce matches 1 if score @s stardew.animal.mood matches ..69 if score #rand stardew.animal.temp > @s stardew.animal.mood run scoreboard players set @s stardew.temp.produce 0

# 如果确定产物，先判断产物类型（鸭蛋还是鸭毛）
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_duck_produce_type
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_egg_quality
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/spawn_duck_produce
