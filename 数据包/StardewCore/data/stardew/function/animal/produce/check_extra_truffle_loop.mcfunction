# ================================================================
# 星露谷物语 - 检查额外松露循环
# ================================================================
# 用途：循环检查是否产出额外松露
# ================================================================

# 生成随机数 0-99
execute store result score #random stardew.animal.temp run random value 0..99

# 如果随机数小于概率，产出额外松露
execute if score #random stardew.animal.temp < #extra_chance stardew.animal.temp run function stardew:animal/produce/produce_truffle
execute if score #random stardew.animal.temp < #extra_chance stardew.animal.temp run scoreboard players add #extra_count stardew.animal.temp 1

# 如果成功且次数小于3，继续检查
execute if score #random stardew.animal.temp < #extra_chance stardew.animal.temp if score #extra_count stardew.animal.temp matches ..2 run function stardew:animal/produce/check_extra_truffle_loop