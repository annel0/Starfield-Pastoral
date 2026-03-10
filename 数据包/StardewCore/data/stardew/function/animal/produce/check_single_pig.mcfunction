# ================================================================
# 星露谷物语 - 单只猪产松露判定
# ================================================================
# 用途：判定一只猪是否产松露
# 调用：从 check_pig_produce.mcfunction 调用

# 初始化产松露标记为1（会产松露）
scoreboard players set @s stardew.temp.produce 1

# 检查是否喂食（没有喂食系统时默认总是喂食）
scoreboard players set @s stardew.animal.fed_today 1

# 检查是否是雨天或雷暴天（雨天不产松露）
execute if score Global sd_weather matches 1 run scoreboard players set @s stardew.temp.produce 0
execute if score Global sd_weather matches 2 run scoreboard players set @s stardew.temp.produce 0

# 检查是否是冬季（冬季不产松露）
execute if score Global sd_season matches 4 run scoreboard players set @s stardew.temp.produce 0

# 如果Mood < 70，进行概率判定（和鸡蛋一样）
execute if score @s stardew.animal.mood matches ..69 store result score #rand stardew.animal.temp run random value 0..70
execute if score @s stardew.animal.mood matches ..69 if score #rand stardew.animal.temp > @s stardew.animal.mood run scoreboard players set @s stardew.temp.produce 0

# 如果确定产松露，计算品质并生成
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_truffle_quality
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/produce_truffle

# 计算额外松露（基于友谊值）
execute if score @s stardew.temp.produce matches 1 run function stardew:animal/produce/calculate_extra_truffles

# 调试信息
execute if score @s stardew.temp.produce matches 1 run tellraw @a[tag=stardew.debug] ["",{"text":"[猪产松露] ","color":"gold"},{"text":"猪 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 产出了松露！","color":"yellow"}]
execute unless score @s stardew.temp.produce matches 1 run tellraw @a[tag=stardew.debug] ["",{"text":"[猪产松露] ","color":"gold"},{"text":"猪 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 因天气条件未产松露","color":"gray"}]