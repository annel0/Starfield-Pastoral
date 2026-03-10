# ================================================================
# 星露谷物语 - 计算测试数值
# ================================================================
# 用途：为一只鸡计算大鸡蛋和品质的数值（不实际产蛋，只显示）

# 保存原始数值
scoreboard players operation #test_friendship stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #test_mood stardew.animal.temp = @s stardew.animal.mood

# ===== 大鸡蛋计算 =====
# 计算 Mood Modifier
scoreboard players operation #mood_modified stardew.animal.temp = @s stardew.animal.mood

execute if score @s stardew.animal.mood matches 201.. run scoreboard players operation #mood_modified stardew.animal.temp *= #3 stardew.animal.temp
execute if score @s stardew.animal.mood matches 201.. run scoreboard players operation #mood_modified stardew.animal.temp /= #2 stardew.animal.temp
execute if score @s stardew.animal.mood matches ..100 run scoreboard players remove #mood_modified stardew.animal.temp 100
execute if score @s stardew.animal.mood matches 101..200 run scoreboard players set #mood_modified stardew.animal.temp 0

# 计算 (Friendship + Mood_Modified)
scoreboard players operation #large_score stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #large_score stardew.animal.temp += #mood_modified stardew.animal.temp

# 除以 1200
scoreboard players operation #large_score_before stardew.animal.temp = #large_score stardew.animal.temp
scoreboard players operation #large_score stardew.animal.temp /= #1200 stardew.animal.temp

tellraw @a [{"text":"[大鸡蛋计算] ","color":"yellow"},{"text":"Friendship(","color":"white"},{"score":{"name":"#test_friendship","objective":"stardew.animal.temp"}},{"text":") + MoodModified(","color":"white"},{"score":{"name":"#mood_modified","objective":"stardew.animal.temp"}},{"text":") = ","color":"white"},{"score":{"name":"#large_score_before","objective":"stardew.animal.temp"}},{"text":" / 1200 = ","color":"white"},{"score":{"name":"#large_score","objective":"stardew.animal.temp"}},{"text":"% 概率","color":"green"}]

# ===== 品质计算 =====
# 计算 Friendship / 1000
scoreboard players operation #friendship_part stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #friendship_part stardew.animal.temp /= #1000 stardew.animal.temp

# 计算 Mood / 225
scoreboard players operation #mood_part stardew.animal.temp = @s stardew.animal.mood
scoreboard players operation #mood_part stardew.animal.temp /= #225 stardew.animal.temp

# 计算 1 - (Mood / 225)
scoreboard players operation #mood_part stardew.animal.temp *= #-1 stardew.animal.temp
scoreboard players operation #mood_part stardew.animal.temp += #1 stardew.animal.temp

# 计算最终得分
scoreboard players operation #quality_score stardew.animal.temp = #friendship_part stardew.animal.temp
scoreboard players operation #quality_score stardew.animal.temp -= #mood_part stardew.animal.temp
scoreboard players operation #quality_score stardew.animal.temp *= #100 stardew.animal.temp

tellraw @a [{"text":"[品质计算] ","color":"yellow"},{"text":"(Friendship/1000) - (1-Mood/225) = ","color":"white"},{"score":{"name":"#quality_score","objective":"stardew.animal.temp"}},{"text":" (乘以100后)","color":"gray"}]
execute if score #test_mood stardew.animal.temp matches ..149 run tellraw @a [{"text":"  ⚠ Mood < 150，无法产出品质鸡蛋","color":"red"}]
execute if score #test_friendship stardew.animal.temp matches ..199 run tellraw @a [{"text":"  ⚠ Friendship < 200，无法产出大鸡蛋","color":"red"}]
