# 接管原版动物成长系统
# 阻止原版自动成长，使用我们的年龄系统

# 先移除所有幼年标记（每tick重新判断）
tag @e[type=#stardew:animals,tag=stardew.animal] remove stardew.animal.is_baby

# 检查是否是幼年动物（Age < 成熟天数）
# 参考星露谷物语官方Wiki数据：
# 鸡舍动物(鸡101/鸭102/兔103): 5天成熟
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 101..103 if score @s stardew.animal.age matches ..4 run tag @s add stardew.animal.is_baby

# 恐龙(104): 6天成熟
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 104 if score @s stardew.animal.age matches ..5 run tag @s add stardew.animal.is_baby

# 虚空鸡(105)金鸡(106): 5天成熟
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 105..106 if score @s stardew.animal.age matches ..4 run tag @s add stardew.animal.is_baby

# 牛(201)山羊(202): 5天成熟  
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 201..202 if score @s stardew.animal.age matches ..4 run tag @s add stardew.animal.is_baby

# 羊(203): 3天成熟
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 203 if score @s stardew.animal.age matches ..2 run tag @s add stardew.animal.is_baby

# 猪(204): 10天成熟
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.type matches 204 if score @s stardew.animal.age matches ..9 run tag @s add stardew.animal.is_baby

# 设置所有动物无敌（每tick强制设置）
execute as @e[type=#stardew:animals,tag=stardew.animal] run data merge entity @s {Invulnerable:1b,PersistenceRequired:1b}

# 阻止幼年动物成长（设置Age为负数让它永远不长大）
execute as @e[type=#stardew:animals,tag=stardew.animal.is_baby] run data merge entity @s {Age:-999999}

# 当达到成熟年龄时，允许它长大一次
execute as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.is_baby] run data merge entity @s {Age:0}
