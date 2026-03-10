# ================================================================
# 星露谷物语 - 单只绵羊产羊毛判定
# ================================================================
# 用途：判定一只绵羊是否长出羊毛
# 调用：从 check_sheep_produce.mcfunction 调用

# 初始化产羊毛标记为1（会产羊毛）
scoreboard players set @s stardew.temp.produce 1

# 检查是否喂食（未喂食则不产羊毛）
execute unless score @s stardew.animal.fed_today matches 1.. run scoreboard players set @s stardew.animal.fed_today 1

# 检查产羊毛周期（每3天产一次）
# 使用produce_days记录距离上次产羊毛的天数
execute unless score @s stardew.animal.produce_days matches 0.. run scoreboard players set @s stardew.animal.produce_days 0
scoreboard players add @s stardew.animal.produce_days 1

# 基础周期：3天
scoreboard players set #required_days stardew.animal.temp 3

# 高友谊度加成：友谊度>=900减少1天
execute if score @s stardew.animal.friendship matches 900.. run scoreboard players remove #required_days stardew.animal.temp 1

# 如果未达到周期，不产羊毛
execute if score @s stardew.animal.produce_days < #required_days stardew.animal.temp run scoreboard players set @s stardew.temp.produce 0

# 如果Mood < 70，进行概率判定
execute if score @s stardew.animal.mood matches ..69 store result score #rand stardew.animal.temp run random value 0..70
execute if score @s stardew.animal.mood matches ..69 if score #rand stardew.animal.temp > @s stardew.animal.mood run scoreboard players set @s stardew.temp.produce 0

# 如果确定产羊毛，计算品质并设置标记
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_sheep_wool_quality
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/spawn_wool_entity

# 设置羊毛已准备好的标记(1=有羊毛可剪)
execute if score @s stardew.temp.produce matches 1 run scoreboard players set @s stardew.animal.has_produce 1

# 保存羊毛类型到animal数据中
execute if score @s stardew.temp.produce matches 1 store result score @s stardew.animal.produce_cmd run scoreboard players get #wool_cmd stardew.animal.temp

# 重置周期计数
execute if score @s stardew.temp.produce matches 1 run scoreboard players set @s stardew.animal.produce_days 0
