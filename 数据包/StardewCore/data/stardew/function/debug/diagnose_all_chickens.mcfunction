# ================================================================
# 诊断所有鸡鸭状态
# ================================================================

tellraw @a {"text":"========== 鸡鸭诊断报告 ==========","color":"gold","bold":true}

# 统计逻辑实体
execute store result score #total_chickens stardew.animal.temp if entity @e[type=chicken,tag=stardew.animal,scores={stardew.animal.type=101}]
execute store result score #total_ducks stardew.animal.temp if entity @e[type=chicken,tag=stardew.animal,scores={stardew.animal.type=102}]

tellraw @a [{"text":"逻辑鸡数量: ","color":"yellow"},{"score":{"name":"#total_chickens","objective":"stardew.animal.temp"},"color":"white"}]
tellraw @a [{"text":"逻辑鸭数量: ","color":"yellow"},{"score":{"name":"#total_ducks","objective":"stardew.animal.temp"},"color":"white"}]

# 统计 AJ 模型
execute store result score #aj_chicken stardew.animal.temp if entity @e[tag=aj.chicken.root,tag=stardew.animal.aj_bound]
execute store result score #aj_chicken_baby stardew.animal.temp if entity @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound,scores={stardew.animal.type=101}]
execute store result score #aj_duck stardew.animal.temp if entity @e[tag=aj.duck.root,tag=stardew.animal.aj_bound]
execute store result score #aj_duck_baby stardew.animal.temp if entity @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound,scores={stardew.animal.type=102}]

tellraw @a [{"text":"AJ 成年鸡模型: ","color":"aqua"},{"score":{"name":"#aj_chicken","objective":"stardew.animal.temp"},"color":"white"}]
tellraw @a [{"text":"AJ 幼年鸡模型: ","color":"aqua"},{"score":{"name":"#aj_chicken_baby","objective":"stardew.animal.temp"},"color":"white"}]
tellraw @a [{"text":"AJ 成年鸭模型: ","color":"aqua"},{"score":{"name":"#aj_duck","objective":"stardew.animal.temp"},"color":"white"}]
tellraw @a [{"text":"AJ 幼年鸭模型: ","color":"aqua"},{"score":{"name":"#aj_duck_baby","objective":"stardew.animal.temp"},"color":"white"}]

tellraw @a {"text":"","color":"gray"}

# 检查每只逻辑鸡
tellraw @a {"text":"=== 逻辑鸡详情 ===","color":"yellow"}
execute as @e[type=chicken,tag=stardew.animal,scores={stardew.animal.type=101}] run function stardew:debug/show_single_chicken_info

tellraw @a {"text":"","color":"gray"}

# 检查每只逻辑鸭
tellraw @a {"text":"=== 逻辑鸭详情 ===","color":"yellow"}
execute as @e[type=chicken,tag=stardew.animal,scores={stardew.animal.type=102}] run function stardew:debug/show_single_duck_info

tellraw @a {"text":"========================================","color":"gold","bold":true}
