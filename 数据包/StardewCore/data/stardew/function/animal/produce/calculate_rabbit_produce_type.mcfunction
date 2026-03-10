# ================================================================
# 星露谷物语 - 兔子产物类型判定
# ================================================================
# 用途：根据友好度判定兔子产羊毛还是兔子脚
# 调用：从 check_single_rabbit.mcfunction 调用
# 
# 逻辑：
# - 基础产物：羊毛（stardew.temp.is_rabbit_foot = 0）
# - 特殊产物：兔子脚（stardew.temp.is_rabbit_foot = 1）
# - 产兔子脚条件：友好度 >= 220
# - 满足条件后约 10% 概率产兔子脚

# 默认产羊毛
scoreboard players set @s stardew.temp.is_rabbit_foot 0

# 检查是否满足产兔子脚的基础条件（友好度 >= 220）
scoreboard players set #can_rabbit_foot stardew.animal.temp 0
execute if score @s stardew.animal.friendship matches 220.. run scoreboard players set #can_rabbit_foot stardew.animal.temp 1

# 如果满足条件，有10%概率产兔子脚
# 根据Wiki：高友好度时有约10%概率产兔子脚
execute if score #can_rabbit_foot stardew.animal.temp matches 1 store result score #foot_chance stardew.animal.temp run random value 1..100
execute if score #can_rabbit_foot stardew.animal.temp matches 1 if score #foot_chance stardew.animal.temp matches ..10 run scoreboard players set @s stardew.temp.is_rabbit_foot 1
