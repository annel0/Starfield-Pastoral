# ================================================================
# 星露谷物语 - 松露品质计算
# ================================================================
# 用途：计算松露的品质（普通/银星/金星/钻石）
# 调用：从 check_single_pig.mcfunction 调用
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
scoreboard players operation #friendship_contrib stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #friendship_contrib stardew.animal.temp *= #100 stardew.animal.const
scoreboard players operation #friendship_contrib stardew.animal.temp /= #1000 stardew.animal.const

# 计算 Mood 贡献（×100 转为百分比）
scoreboard players set #225 stardew.animal.const 225
scoreboard players operation #mood_contrib stardew.animal.temp = @s stardew.animal.mood
scoreboard players operation #mood_contrib stardew.animal.temp *= #100 stardew.animal.const
scoreboard players operation #mood_contrib stardew.animal.temp /= #225 stardew.animal.const

# 计算最终得分：friendship% + mood% - 100%
scoreboard players operation #quality_score stardew.animal.temp = #friendship_contrib stardew.animal.temp
scoreboard players operation #quality_score stardew.animal.temp += #mood_contrib stardew.animal.temp
scoreboard players operation #quality_score stardew.animal.temp -= #100 stardew.animal.const

# 根据得分设置品质
# >= 20% 为银星
execute if score #quality_score stardew.animal.temp matches 20.. run scoreboard players set @s stardew.temp.quality 1

# >= 40% 为金星
execute if score #quality_score stardew.animal.temp matches 40.. run scoreboard players set @s stardew.temp.quality 2

# >= 60% 为钻石
execute if score #quality_score stardew.animal.temp matches 60.. run scoreboard players set @s stardew.temp.quality 3

tellraw @a[tag=stardew.debug] [{"text":"[松露品质] ","color":"brown"},{"text":"友谊:"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":" 心情:"},{"score":{"name":"@s","objective":"stardew.animal.mood"}},{"text":" 得分:"},{"score":{"name":"#quality_score","objective":"stardew.animal.temp"}},{"text":"% 品质:"},{"score":{"name":"@s","objective":"stardew.temp.quality"}}]