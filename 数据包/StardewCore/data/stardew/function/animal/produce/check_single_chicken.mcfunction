# ================================================================
# 星露谷物语 - 单只鸡产蛋判定
# ================================================================
# 用途：判定一只鸡是否产蛋
# 调用：从 check_chicken_produce.mcfunction 调用

# 初始化产蛋标记为1（会产蛋）
scoreboard players set @s stardew.temp.produce 1

# 检查是否喂食（未喂食则不产蛋）
# 注意：如果 fed_today 没有被设置过（没有喂食系统），默认允许产蛋
# 只有当 fed_today 明确设置为 -1 时才阻止产蛋（用于未喂食的情况）
execute unless score @s stardew.animal.fed_today matches 1.. run scoreboard players set @s stardew.animal.fed_today 1

# 如果Mood < 70，进行概率判定
# 公式：产蛋概率 = Mood / 70
# 这里简化为：生成0-70的随机数，如果随机数 > Mood 则不产蛋
execute if score @s stardew.animal.mood matches ..69 store result score #rand stardew.animal.temp run random value 0..70
execute if score @s stardew.animal.mood matches ..69 if score #rand stardew.animal.temp > @s stardew.animal.mood run scoreboard players set @s stardew.temp.produce 0

# 如果确定产蛋，计算大小和品质
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_large_egg
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_egg_quality
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/spawn_egg_entity
