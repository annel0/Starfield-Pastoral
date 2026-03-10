# ================================================================
# 星露谷物语 - 测试鸭子产物（给所有鸭子设置理想数值）
# ================================================================
# 用途：为测试产物系统，给所有鸭子设置好的friendship和mood值
# 调用：手动执行 /function stardew:animal/debug/test_duck_production

# 给所有鸭子设置数值
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 102 run scoreboard players set @s stardew.animal.friendship 500
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 102 run scoreboard players set @s stardew.animal.mood 200
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 102 run scoreboard players set @s stardew.animal.fed_today 1
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 102 run scoreboard players set @s stardew.animal.age 6

tellraw @a [{"text":"[测试] ","color":"yellow"},{"text":"已为所有鸭子设置测试数值: Friendship=500, Mood=200, Age=6, Fed=1","color":"green"}]

# 显示当前所有鸭子的状态
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 102 run tellraw @a [{"text":"鸭子 #","color":"aqua"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" | 年龄:"},{"score":{"name":"@s","objective":"stardew.animal.age"}},{"text":" | 好感:"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":" | 心情:"},{"score":{"name":"@s","objective":"stardew.animal.mood"}},{"text":" | 建筑:"},{"score":{"name":"@s","objective":"stardew.animal.building"}}]
