# ================================================================
# 检查兔是否需要从幼年模型切换到成年模型
# ================================================================
# @s = 兔逻辑实体
# 需要 #check_id 已设置

# 检查是否有幼年兔模型存在
execute store result score #has_baby_model stardew.animal.temp if entity @e[tag=aj.rabbit_baby.root,tag=stardew.animal.aj_bound,scores={stardew.animal.type=103}] if score @s stardew.animal.id = #check_id stardew.animal.temp

# 如果有幼年模型且已满5天，执行切换
execute if score #has_baby_model stardew.animal.temp matches 1.. run function stardew:animal/animated_java/switch_rabbit_baby_to_adult
