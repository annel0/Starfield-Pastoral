# ================================================================
# 星露谷物语 - 动物回家移动逻辑
# ================================================================
# 用途：每秒执行，让正在回家的动物移动
# 调用：从主tick调用（每秒一次，用计时器控制）

# 处理所有正在回家的动物
execute as @e[type=#stardew:animals,tag=stardew.animal.going_home] at @s run function stardew:building/animal/move_towards_home
