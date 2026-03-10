# ================================================================
# 星露谷物语 - 大鸡蛋计算
# ================================================================
# 用途：判定是否产大鸡蛋
# 调用：从 check_single_chicken.mcfunction 调用
# 公式：(Friendship + (Mood × Mood Modifier)) / 1200
# 需要 Friendship >= 200

# 初始化为普通鸡蛋
scoreboard players set @s stardew.temp.is_large 0

# 检查 Friendship 是否 >= 200
execute if score @s stardew.animal.friendship matches ..199 run return 0

# 计算 Mood Modifier
# 如果 Mood > 200: Modifier = 1.5 (实际计算为 Mood * 3 / 2)
# 如果 Mood <= 100: Modifier = Mood - 100 (负数)
# 如果 100 < Mood <= 200: Modifier = 0

scoreboard players operation #mood_modified stardew.animal.temp = @s stardew.animal.mood

execute if score @s stardew.animal.mood matches 201.. run scoreboard players operation #mood_modified stardew.animal.temp *= #3 stardew.animal.temp
execute if score @s stardew.animal.mood matches 201.. run scoreboard players operation #mood_modified stardew.animal.temp /= #2 stardew.animal.temp

execute if score @s stardew.animal.mood matches ..100 run scoreboard players remove #mood_modified stardew.animal.temp 100

execute if score @s stardew.animal.mood matches 101..200 run scoreboard players set #mood_modified stardew.animal.temp 0

# 计算 (Friendship + Mood_Modified)
scoreboard players operation #large_score stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #large_score stardew.animal.temp += #mood_modified stardew.animal.temp

# 乘以100再除以1200，得到百分比概率（避免整数除法损失精度）
scoreboard players operation #large_score stardew.animal.temp *= #100 stardew.animal.temp
scoreboard players operation #large_score stardew.animal.temp /= #1200 stardew.animal.temp

# 生成 0-100 的随机数进行判定
execute store result score #rand stardew.animal.temp run random value 0..100

# 如果 large_score > rand，则产大鸡蛋
execute if score #large_score stardew.animal.temp > #rand stardew.animal.temp run scoreboard players set @s stardew.temp.is_large 1
