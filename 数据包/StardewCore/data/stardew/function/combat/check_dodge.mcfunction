# 闪避判定
# 如果闪避成功，完全避免伤害

# 随机roll 1-100
execute store result score #dodge_roll sd_temp run random value 1..100

# 获取玩家的闪避率
scoreboard players operation #dodge_chance sd_temp = @s sd_dodge_chance

# 如果roll值 <= 闪避率，则闪避成功
execute if score #dodge_roll sd_temp <= #dodge_chance sd_temp run function stardew:combat/dodge_success

# 如果闪避失败，继续正常伤害流程（不做任何处理）
