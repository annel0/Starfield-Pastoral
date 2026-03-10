# ================================================================
# 星露谷物语 - 检测是否在看着动物
# ================================================================
# 用途：玩家潜行+空手时，检测是否在看着动物并累积时间
# @s = 玩家
# ================================================================

# 射线检测：检查前方3格内是否有动物
execute anchored eyes positioned ^ ^ ^0.5 if entity @e[type=#stardew:animals,tag=stardew.animal,distance=..1] run scoreboard players add @s stardew.animal.temp 1
execute anchored eyes positioned ^ ^ ^1.0 if entity @e[type=#stardew:animals,tag=stardew.animal,distance=..1] run scoreboard players add @s stardew.animal.temp 1
execute anchored eyes positioned ^ ^ ^1.5 if entity @e[type=#stardew:animals,tag=stardew.animal,distance=..1] run scoreboard players add @s stardew.animal.temp 1
execute anchored eyes positioned ^ ^ ^2.0 if entity @e[type=#stardew:animals,tag=stardew.animal,distance=..1] run scoreboard players add @s stardew.animal.temp 1
execute anchored eyes positioned ^ ^ ^2.5 if entity @e[type=#stardew:animals,tag=stardew.animal,distance=..1] run scoreboard players add @s stardew.animal.temp 1
execute anchored eyes positioned ^ ^ ^3.0 if entity @e[type=#stardew:animals,tag=stardew.animal,distance=..1] run scoreboard players add @s stardew.animal.temp 1

# 如果累积到10tick（0.5秒），执行抚摸
execute if score @s stardew.animal.temp matches 10.. run function stardew:animal/interact/try_pet_animal
execute if score @s stardew.animal.temp matches 10.. run scoreboard players set @s stardew.animal.temp 0
