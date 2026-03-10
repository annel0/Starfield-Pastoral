# ================================================================
# 星露谷物语 - 单只牛产奶判定
# ================================================================
# 用途：判定一只牛是否产奶
# 调用：从 check_cow_produce.mcfunction 调用

# 初始化产奶标记为1（会产奶）
scoreboard players set @s stardew.temp.produce 1

# 检查是否喂食（未喂食则不产奶）
# 注意：如果 fed_today 没有被设置过（没有喂食系统），默认允许产奶
# 只有当 fed_today 明确设置为 -1 时才阻止产奶（用于未喂食的情况）
execute unless score @s stardew.animal.fed_today matches 1.. run scoreboard players set @s stardew.animal.fed_today 1

# 如果Mood < 70，进行概率判定
# 公式：产奶概率 = Mood / 70
# 这里简化为：生成0-70的随机数，如果随机数 > Mood 则不产奶
execute if score @s stardew.animal.mood matches ..69 store result score #rand stardew.animal.temp run random value 0..70
execute if score @s stardew.animal.mood matches ..69 if score #rand stardew.animal.temp > @s stardew.animal.mood run scoreboard players set @s stardew.temp.produce 0

# 如果确定产奶，计算大小和品质并设置标记
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_large_milk
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_milk_quality
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/spawn_milk_entity

# 设置牛奶已准备好的标记(1=有奶可挤)
execute if score @s stardew.temp.produce matches 1 run scoreboard players set @s stardew.animal.has_produce 1

# 保存牛奶类型到animal数据中
execute if score @s stardew.temp.produce matches 1 store result score @s stardew.animal.produce_cmd run scoreboard players get #milk_cmd stardew.animal.temp
