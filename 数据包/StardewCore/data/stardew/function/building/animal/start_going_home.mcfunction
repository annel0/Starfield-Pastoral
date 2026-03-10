# ================================================================
# 星露谷物语 - 晚上动物回家管理器
# ================================================================
# 用途：18:00触发，让所有动物回家
# 调用：从time系统在18:00时调用

# 标记所有需要回家的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id matches 1.. run tag @s add stardew.animal.going_home
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id matches 1.. run scoreboard players set @s stardew.animal.going_home 1

tellraw @a [{"text":"[建筑系统] ","color":"gold"},{"text":"18:00 - 动物们开始回家（记得开门让它们进来）","color":"yellow"}]
