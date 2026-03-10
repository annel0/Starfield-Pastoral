# ================================================================
# 星露谷物语 - 建筑交互处理
# ================================================================
# 用途：处理玩家点击建筑（左键删除，右键开关门）
# 调用：从manage_interactions调用，作为interaction实体执行

# 找到对应的marker建筑ID
execute store result score #temp_id stardew.building.temp run scoreboard players get @s stardew.building.id

# 处理左键 - 删除建筑
execute if data entity @s attack at @s as @e[type=marker,tag=stardew.building,distance=..2] if score @s stardew.building.id = #temp_id stardew.building.temp run function stardew:building/interact/delete_building

# 处理右键 - 切换门状态
execute if data entity @s interaction at @s as @e[type=marker,tag=stardew.building,distance=..2] if score @s stardew.building.id = #temp_id stardew.building.temp run function stardew:building/interact/toggle_door

# 清除交互数据
data remove entity @s attack
data remove entity @s interaction
