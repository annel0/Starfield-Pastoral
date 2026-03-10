# ================================================================
# 星露谷物语 - 绑定动物到建筑
# ================================================================
# 用途：设置动物的building_id为最近的建筑ID
# 调用：从bind_nearest_animal调用，作为动物执行

# 找到最近的建筑
tag @e[type=marker,tag=stardew.building] remove stardew.temp.nearest_building
tag @e[type=marker,tag=stardew.building,distance=..20,limit=1,sort=nearest] add stardew.temp.nearest_building

# 复制建筑ID到动物
execute if entity @e[type=marker,tag=stardew.temp.nearest_building] run scoreboard players operation @s stardew.animal.building_id = @e[type=marker,tag=stardew.temp.nearest_building,limit=1] stardew.building.id

# 初始化状态（默认在里面）
tag @s remove stardew.animal.is_outside
scoreboard players set @s stardew.animal.is_outside 0

# 显示消息
execute if entity @e[type=marker,tag=stardew.temp.nearest_building] run tellraw @a [{"text":"[建筑] ","color":"gold"},{"text":"已将动物绑定到建筑 ID: ","color":"green"},{"score":{"name":"@s","objective":"stardew.animal.building_id"}}]

# 清除标签
tag @e remove stardew.temp.nearest_building
