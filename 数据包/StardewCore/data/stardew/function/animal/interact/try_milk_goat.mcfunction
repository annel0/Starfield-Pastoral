# ================================================================
# 星露谷物语 - 尝试挤山羊奶
# ================================================================
# 用途：检查山羊是否有羊奶可挤，如果有则收集羊奶
# @s = 山羊实体（使用sheep实体，type=202）
# ================================================================

# 检查山羊是否有羊奶可挤
execute unless score @s stardew.animal.has_produce matches 1 run tellraw @a[distance=..5] [{"text":"[挤奶] ","color":"yellow","bold":true},{"text":"这只山羊现在没有羊奶可挤!","color":"white","bold":false}]
execute unless score @s stardew.animal.has_produce matches 1 run return 0

# 有羊奶可挤，执行挤奶
function stardew:animal/interact/milk_goat_success
