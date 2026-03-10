# ================================================================
# 修复单只鸭子
# ================================================================
# @s = 需要长大的鸭子逻辑实体

# 设置 check_id
scoreboard players operation #check_id stardew.animal.temp = @s stardew.animal.id

# 检查是否有幼年模型
execute store result score #has_baby stardew.animal.temp as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #check_id stardew.animal.temp if score @s stardew.animal.type matches 102 run return 1

# 如果有幼年模型，执行切换
execute if score #has_baby stardew.animal.temp matches 1.. run function stardew:animal/animated_java/check_duck_growth
execute if score #has_baby stardew.animal.temp matches 1.. run scoreboard players add #fixed_count stardew.animal.temp 1

# 如果没有幼年模型，可能是模型丢失了，重新生成
execute unless score #has_baby stardew.animal.temp matches 1.. at @s run function stardew:animal/animated_java/summon_duck
execute unless score #has_baby stardew.animal.temp matches 1.. run tellraw @a [{"text":"[警告] 鸭子 ID ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"},{"text":" 的幼年模型丢失，已重新生成成年模型","color":"red"}]
