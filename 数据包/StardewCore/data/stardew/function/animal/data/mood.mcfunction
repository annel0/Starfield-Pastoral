# ================================================================
# 星露谷物语 - 心情计算系统
# ================================================================
# 用途：计算和更新动物的心情值
# 心情范围：0-255
# ================================================================

# 此函数由交互系统调用，对当前交互的动物执行
# @s = 被交互的动物实体

# ================================================================
# 增加心情值
# ================================================================

# 抚摸增加心情
# 基础值: +32 到 +36，这里用 +34
# TODO: 如果玩家有牧羊人/养鸡人职业，翻倍
scoreboard players add @s stardew.animal.mood 34

# 限制最大值为255
execute if score @s stardew.animal.mood matches 256.. run scoreboard players set @s stardew.animal.mood 255

# ================================================================
# 心情等级显示
# ================================================================

# 显示心情状态
execute if score @s stardew.animal.mood matches 200.. run tellraw @a[distance=..5] [{"text":"[心情] ","color":"green","bold":true},{"text":"看起来非常开心！😊","color":"white","bold":false}]
execute if score @s stardew.animal.mood matches 30..199 run tellraw @a[distance=..5] [{"text":"[心情] ","color":"yellow","bold":true},{"text":"看起来还不错。","color":"white","bold":false}]
execute if score @s stardew.animal.mood matches ..29 run tellraw @a[distance=..5] [{"text":"[心情] ","color":"red","bold":true},{"text":"看起来很难过...😢","color":"white","bold":false}]
