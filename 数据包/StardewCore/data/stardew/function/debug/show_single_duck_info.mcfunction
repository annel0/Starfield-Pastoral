# ================================================================
# 显示单个鸭的信息
# ================================================================
# @s = 逻辑鸭

# 保存 ID
scoreboard players operation #show_id stardew.animal.temp = @s stardew.animal.id

# 检查是否有对应的 AJ 模型
execute store result score #has_aj stardew.animal.temp if entity @e[tag=aj.duck.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = @e[tag=aj.duck.root,tag=stardew.animal.aj_bound,limit=1] stardew.animal.id
execute store result score #has_baby_aj stardew.animal.temp if entity @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound,scores={stardew.animal.type=102}] if score @s stardew.animal.id = @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound,scores={stardew.animal.type=102},limit=1] stardew.animal.id

tellraw @a [{"text":"鸭 ID=","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"green"},{"text":" 年龄=","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"aqua"},{"text":" 成年模型=","color":"white"},{"score":{"name":"#has_aj","objective":"stardew.animal.temp"},"color":"yellow"},{"text":" 幼年模型=","color":"white"},{"score":{"name":"#has_baby_aj","objective":"stardew.animal.temp"},"color":"yellow"}]
