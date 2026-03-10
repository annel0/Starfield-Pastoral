# ================================================================
# 星露谷物语 - ID栈管理系统
# ================================================================
# 使用栈结构管理已删除的建筑ID，实现ID重用

# 栈存储方案：
# - 鸡舍ID栈：使用盔甲架实体存储在世界边界
# - 每个盔甲架的 stardew.building.id 存储一个可用ID
# - 标签：stardew.id_stack.coop 或 stardew.id_stack.barn

# ================================================================
# 回收鸡舍ID到栈
# ================================================================
# 调用：从delete_building调用
# 前提：#delete_id 已保存要回收的ID

# 检查ID是否在有效范围内（1-10）
execute unless score #delete_id stardew.building.temp matches 1..10 run return 0

# 检查栈中是否已有此ID（避免重复）
execute if entity @e[type=marker,tag=stardew.id_stack.coop] if score @s stardew.building.id = #delete_id stardew.building.temp run return 0

# 在世界边界创建栈节点（marker实体）
summon marker 0 -64 0 {Tags:["stardew.id_stack.coop","stardew.id_stack"]}

# 设置ID
execute as @e[type=marker,tag=stardew.id_stack.coop,tag=!stardew.id_set] run scoreboard players operation @s stardew.building.id = #delete_id stardew.building.temp
execute as @e[type=marker,tag=stardew.id_stack.coop,tag=!stardew.id_set] run tag @s add stardew.id_set

tellraw @a[tag=debug] [{"text":"[ID栈] ","color":"aqua"},{"text":"回收鸡舍ID: "},{"score":{"name":"#delete_id","objective":"stardew.building.temp"}}]
