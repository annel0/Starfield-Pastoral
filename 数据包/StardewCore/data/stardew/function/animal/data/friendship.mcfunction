# ================================================================
# 星露谷物语 - 好感度计算系统
# ================================================================
# 用途：计算和更新动物的好感度
# 好感度范围：0-1000
# ================================================================

# 此函数由交互系统调用，对当前交互的动物执行
# @s = 被交互的动物实体

# ================================================================
# 增加好感度
# ================================================================

# 抚摸增加好感度
# 基础值: +15
# TODO: 如果玩家有牧羊人/养鸡人职业，增加到 +30
execute if score @s stardew.animal.friendship_today matches 0 run scoreboard players add @s stardew.animal.friendship 15

# 记录今日已抚摸
execute if score @s stardew.animal.friendship_today matches 0 run scoreboard players set @s stardew.animal.friendship_today 1

# 限制最大值为1000
execute if score @s stardew.animal.friendship matches 1001.. run scoreboard players set @s stardew.animal.friendship 1000

# ================================================================
# 好感度等级显示
# ================================================================

# 计算心数 (每200点 = 1颗心)
scoreboard players operation @s stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation @s stardew.animal.temp /= #200 stardew.animal.temp

# 显示好感度
execute if score @s stardew.animal.temp matches 0 run tellraw @a[distance=..5] [{"text":"❤ ","color":"gray"},{"text":"☆☆☆☆☆","color":"gray"},{"text":" (","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"yellow"},{"text":"/1000)","color":"white"}]
execute if score @s stardew.animal.temp matches 1 run tellraw @a[distance=..5] [{"text":"❤ ","color":"red"},{"text":"★","color":"gold"},{"text":"☆☆☆☆","color":"gray"},{"text":" (","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"yellow"},{"text":"/1000)","color":"white"}]
execute if score @s stardew.animal.temp matches 2 run tellraw @a[distance=..5] [{"text":"❤ ","color":"red"},{"text":"★★","color":"gold"},{"text":"☆☆☆","color":"gray"},{"text":" (","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"yellow"},{"text":"/1000)","color":"white"}]
execute if score @s stardew.animal.temp matches 3 run tellraw @a[distance=..5] [{"text":"❤ ","color":"red"},{"text":"★★★","color":"gold"},{"text":"☆☆","color":"gray"},{"text":" (","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"yellow"},{"text":"/1000)","color":"white"}]
execute if score @s stardew.animal.temp matches 4 run tellraw @a[distance=..5] [{"text":"❤ ","color":"red"},{"text":"★★★★","color":"gold"},{"text":"☆","color":"gray"},{"text":" (","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"yellow"},{"text":"/1000)","color":"white"}]
execute if score @s stardew.animal.temp matches 5.. run tellraw @a[distance=..5] [{"text":"❤ ","color":"red"},{"text":"★★★★★","color":"gold"},{"text":" (","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"yellow"},{"text":"/1000)","color":"white"}]
