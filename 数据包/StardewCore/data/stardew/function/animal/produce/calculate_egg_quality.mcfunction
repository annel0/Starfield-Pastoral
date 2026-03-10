# ================================================================
# 星露谷物语 - 鸡蛋品质计算
# ================================================================
# 用途：计算鸡蛋的品质（普通/银星/金星/钻石）
# 调用：从 check_single_chicken.mcfunction 调用
# 公式：(Friendship / 1000) - (1 - (Mood / 225))
# 需要 Mood >= 150

# 初始化为普通品质（0=base, 1=silver, 2=gold, 3=diamond）
scoreboard players set @s stardew.temp.quality 0

# 检查 Mood 是否 >= 150
execute if score @s stardew.animal.mood matches ..149 run return 0

# 计算品质得分，避免整数除法问题
# 公式：(Friendship / 1000) - (1 - (Mood / 225))
# 改为：(Friendship × 225 - 1000 × (225 - Mood)) / (1000 × 225)
# 为了避免溢出，简化为百分比计算

# 先计算 Friendship 贡献（×100 转为百分比）
scoreboard players operation #friendship_score stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #friendship_score stardew.animal.temp *= #100 stardew.animal.temp
scoreboard players operation #friendship_score stardew.animal.temp /= #1000 stardew.animal.temp

# 再计算 Mood 贡献（×100 转为百分比）
scoreboard players operation #mood_score stardew.animal.temp = @s stardew.animal.mood
scoreboard players operation #mood_score stardew.animal.temp *= #100 stardew.animal.temp
scoreboard players operation #mood_score stardew.animal.temp /= #225 stardew.animal.temp

# 计算 100 - mood_score
scoreboard players set #100_temp stardew.animal.temp 100
scoreboard players operation #100_temp stardew.animal.temp -= #mood_score stardew.animal.temp

# 最终得分 = friendship_score - (100 - mood_score)
scoreboard players operation #quality_score stardew.animal.temp = #friendship_score stardew.animal.temp
scoreboard players operation #quality_score stardew.animal.temp -= #100_temp stardew.animal.temp

# 品质判定（quality_score 已经是百分比了）
# 钻石：score > 95 且 (score/2) > random(0-100)
# 金星：(score/2) > random(0-100)
# 银星：score > random(0-100)

# 生成随机数
execute store result score #rand stardew.animal.temp run random value 0..100

# 先判定银星
execute if score #quality_score stardew.animal.temp > #rand stardew.animal.temp run scoreboard players set @s stardew.temp.quality 1

# 再判定金星（score/2 > rand）
scoreboard players operation #half_score stardew.animal.temp = #quality_score stardew.animal.temp
scoreboard players operation #half_score stardew.animal.temp /= #2 stardew.animal.temp
execute if score #half_score stardew.animal.temp > #rand stardew.animal.temp run scoreboard players set @s stardew.temp.quality 2

# 最后判定钻石（需要 score > 95 且 half_score > rand）
execute if score #quality_score stardew.animal.temp > #95 stardew.animal.temp if score #half_score stardew.animal.temp > #rand stardew.animal.temp run scoreboard players set @s stardew.temp.quality 3
