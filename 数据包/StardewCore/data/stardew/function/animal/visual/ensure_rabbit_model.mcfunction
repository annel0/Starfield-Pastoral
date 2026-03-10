# ================================================================
# 确保兔子有正确的模型
# ================================================================
# @s = 兔子逻辑实体（type=103）

# 保存ID
scoreboard players operation #check_id stardew.animal.temp = @s stardew.animal.id

# 检查是否有成年兔模型
execute store result score #has_adult_model stardew.animal.temp if entity @e[tag=aj.rabbit.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #check_id stardew.animal.temp

# 检查是否有幼年兔模型
execute store result score #has_baby_model stardew.animal.temp if entity @e[tag=aj.rabbit_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #check_id stardew.animal.temp if score @s stardew.animal.type matches 103

# 如果年龄 < 5 且没有幼年模型，生成幼年模型
execute if score @s stardew.animal.age matches ..4 unless score #has_baby_model stardew.animal.temp matches 1.. run function stardew:animal/animated_java/summon_rabbit_baby

# 如果年龄 >= 5 且没有成年模型，生成成年模型
execute if score @s stardew.animal.age matches 5.. unless score #has_adult_model stardew.animal.temp matches 1.. run function stardew:animal/animated_java/summon_rabbit

# 如果年龄 >= 5 但还有幼年模型，切换到成年
execute if score @s stardew.animal.age matches 5.. if score #has_baby_model stardew.animal.temp matches 1.. unless score #has_adult_model stardew.animal.temp matches 1.. run function stardew:animal/animated_java/check_rabbit_growth
