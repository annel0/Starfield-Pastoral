# ================================================================
# 星露谷物语 - 回收畜棚ID到栈
# ================================================================
# 调用：从delete_building调用
# 前提：#delete_id 已保存要回收的ID

# 检查ID是否在有效范围内（11-20）
execute unless score #delete_id stardew.building.temp matches 11..20 run return 0

# 检查栈中是否已有此ID
execute if entity @e[type=marker,tag=stardew.id_stack.barn] if score @s stardew.building.id = #delete_id stardew.building.temp run return 0

# 创建栈节点
summon marker 0 -64 0 {Tags:["stardew.id_stack.barn","stardew.id_stack"]}

# 设置ID
execute as @e[type=marker,tag=stardew.id_stack.barn,tag=!stardew.id_set] run scoreboard players operation @s stardew.building.id = #delete_id stardew.building.temp
execute as @e[type=marker,tag=stardew.id_stack.barn,tag=!stardew.id_set] run tag @s add stardew.id_set

tellraw @a[tag=debug] [{"text":"[ID栈] ","color":"aqua"},{"text":"回收畜棚ID: "},{"score":{"name":"#delete_id","objective":"stardew.building.temp"}}]
