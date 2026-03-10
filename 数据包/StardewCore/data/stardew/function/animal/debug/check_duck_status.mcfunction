# ================================================================
# 调试鸭子成长问题
# ================================================================
# 用途：检查鸭子的年龄和模型状态

tellraw @a [{"text":"=== 鸭子成长调试信息 ===","color":"gold","bold":true}]

# 检查所有鸭子逻辑实体
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 102 run tellraw @a [{"text":"[逻辑鸭] ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"},{"text":" 年龄: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animal.age"},"color":"white"},{"text":"天","color":"white"}]

# 检查所有幼年鸭模型
execute as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 102 run tellraw @a [{"text":"[幼年鸭AJ] ID: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"},{"text":" Type: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.type"},"color":"white"}]

# 检查所有成年鸭模型
execute as @e[tag=aj.duck.root,tag=stardew.animal.aj_bound] run tellraw @a [{"text":"[成年鸭AJ] ID: ","color":"aqua"},{"score":{"name":"@s","objective":"stardew.animal.id"},"color":"white"},{"text":" Type: ","color":"aqua"},{"score":{"name":"@s","objective":"stardew.animal.type"},"color":"white"}]

tellraw @a [{"text":"==================","color":"gold","bold":true}]
