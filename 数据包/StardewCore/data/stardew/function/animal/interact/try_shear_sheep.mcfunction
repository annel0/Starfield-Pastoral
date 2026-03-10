# ================================================================
# 星露谷物语 - 尝试剪羊毛
# ================================================================
# 用途：检查绵羊是否有羊毛可剪,如果有则收集羊毛
# 调用：从 on_interaction_clicked.mcfunction 调用
# @s = 绵羊实体

# 检查绵羊是否有羊毛可剪
execute unless score @s stardew.animal.has_produce matches 1 run tellraw @a[distance=..5] [{"text":"[剪羊毛] ","color":"yellow","bold":true},{"text":"这只绵羊的羊毛还没长出来!","color":"white","bold":false}]
execute unless score @s stardew.animal.has_produce matches 1 run return 0

# 有羊毛可剪,执行剪羊毛
function stardew:animal/interact/shear_sheep_success
