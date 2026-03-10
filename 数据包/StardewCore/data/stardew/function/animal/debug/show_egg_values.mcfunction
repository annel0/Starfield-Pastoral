# ================================================================
# 星露谷物语 - 调试鸡蛋生成数值
# ================================================================
# 用途：显示所有鸡的产蛋相关数值
# 调用：手动执行 /function stardew:animal/debug/show_egg_values

tellraw @a [{"text":"==================== 鸡蛋产出数值 ====================","color":"gold"}]

execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 101 run tellraw @a [{"text":"鸡 #","color":"aqua"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" | 好感:","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.friendship"},"color":"white"},{"text":" | 心情:","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.mood"},"color":"white"},{"text":" | 年龄:","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"white"},{"text":" | 喂食:","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.fed_today"},"color":"white"}]

# 手动计算一次大鸡蛋概率（针对第一只鸡）
execute as @e[type=#stardew:animals,tag=stardew.animal,limit=1] if score @s stardew.animal.type matches 101 run function stardew:animal/debug/calculate_test_values

tellraw @a [{"text":"===================================================","color":"gold"}]
