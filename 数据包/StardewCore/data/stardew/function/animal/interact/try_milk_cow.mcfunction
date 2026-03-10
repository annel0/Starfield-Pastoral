# ================================================================
# 星露谷物语 - 尝试挤奶
# ================================================================
# 用途：检查牛是否有奶可挤,如果有则收集牛奶
# 调用：从 milk_cow_check.mcfunction 调用
# @s = 牛实体

# 检查牛是否有奶可挤
execute unless score @s stardew.animal.has_produce matches 1 run tellraw @a[distance=..5] [{"text":"[挤奶] ","color":"yellow","bold":true},{"text":"这头牛现在没有奶可挤!","color":"white","bold":false}]
execute unless score @s stardew.animal.has_produce matches 1 run return 0

# 有奶可挤,执行挤奶
function stardew:animal/interact/milk_cow_success
