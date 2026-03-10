# ================================================================
# 星露谷物语 - 单只兔子产物判定
# ================================================================
# 用途：判定一只兔子是否产物（每天产一次）
# @s = 兔子逻辑实体

# 初始化产物标记为0（不产）
scoreboard players set @s stardew.temp.produce 0

# 检查是否成年（年龄至少5天）
execute unless score @s stardew.animal.age matches 5.. run return 0

# 检查是否喂食（未喂食则不产物）
execute unless score @s stardew.animal.fed_today matches 1.. run return 0

# 兔子每天都产物
scoreboard players set @s stardew.temp.produce 1

# 如果Mood < 70，进行概率判定（与鸡相同的逻辑）
execute if score @s stardew.temp.produce matches 1 if score @s stardew.animal.mood matches ..69 store result score #rand stardew.animal.temp run random value 0..70
execute if score @s stardew.temp.produce matches 1 if score @s stardew.animal.mood matches ..69 if score #rand stardew.animal.temp > @s stardew.animal.mood run scoreboard players set @s stardew.temp.produce 0

# 如果确定产物，先判断产物类型（羊毛还是兔子脚）
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_rabbit_produce_type
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_wool_quality
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/spawn_rabbit_produce
