# ================================================================
# 确保鸭有正确的模型
# ================================================================
# @s = 鸭逻辑实体

# 保存ID和年龄
scoreboard players operation #check_id stardew.animal.temp = @s stardew.animal.id
scoreboard players operation #check_age stardew.animal.temp = @s stardew.animal.age

# 检查是否有对应的AJ模型（幼年或成年）
execute store result score #has_model stardew.animal.temp if entity @e[tag=aj.duck.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #check_id stardew.animal.temp
execute store result score #has_baby_model stardew.animal.temp if entity @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #check_id stardew.animal.temp if score @s stardew.animal.type matches 102
scoreboard players operation #has_model stardew.animal.temp += #has_baby_model stardew.animal.temp

# 如果没有模型，根据年龄生成
execute if score #has_model stardew.animal.temp matches 0 if score #check_age stardew.animal.temp matches ..4 at @s run function stardew:animal/animated_java/summon_duck_baby
execute if score #has_model stardew.animal.temp matches 0 if score #check_age stardew.animal.temp matches 5.. at @s run function stardew:animal/animated_java/summon_duck
