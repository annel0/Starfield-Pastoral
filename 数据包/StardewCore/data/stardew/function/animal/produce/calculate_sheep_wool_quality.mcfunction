# ================================================================
# 星露谷物语 - 绵羊羊毛品质计算
# ================================================================
# 用途：计算绵羊羊毛的品质（普通/银星/金星/铱星）
# 调用：从 check_single_sheep.mcfunction 调用
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
# 铱星：score > 95 且 (score/2) > random(0-100)
# 金星：(score/2) > random(0-100)
# 银星：score > 80 且友谊度>=900

# 随机值用于品质判定
execute store result score #rand_quality stardew.animal.temp run random value 0..100

# 计算 quality_score/2
scoreboard players operation #half_quality stardew.animal.temp = #quality_score stardew.animal.temp
scoreboard players operation #half_quality stardew.animal.temp /= #2 stardew.animal.temp

# 判定铱星品质（最高品质）
execute if score #quality_score stardew.animal.temp matches 96.. if score #half_quality stardew.animal.temp > #rand_quality stardew.animal.temp run scoreboard players set @s stardew.temp.quality 3

# 判定金星品质
execute if score @s stardew.temp.quality matches 0 if score #half_quality stardew.animal.temp > #rand_quality stardew.animal.temp run scoreboard players set @s stardew.temp.quality 2

# 判定银星品质
execute if score @s stardew.temp.quality matches 0 if score #quality_score stardew.animal.temp matches 80.. if score @s stardew.animal.friendship matches 900.. run scoreboard players set @s stardew.temp.quality 1
