# ================================================================
# 星露谷物语 - 删除建筑
# ================================================================
# 用途：删除建筑及其所有相关实体，回收ID到栈
# 调用：从on_clicked调用，作为marker建筑执行

# 保存建筑ID和类型用于回收
scoreboard players operation #delete_id stardew.building.temp = @s stardew.building.id
scoreboard players operation #delete_type stardew.building.temp = @s stardew.building.type

# 显示消息
tellraw @a[distance=..20] [{"text":"[建筑] ","color":"gold"},{"text":"已删除建筑 ID: ","color":"red"},{"score":{"name":"@s","objective":"stardew.building.id"}}]

# 解绑所有属于这个建筑的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id = #delete_id stardew.building.temp run scoreboard players reset @s stardew.animal.building_id
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.building_id = #delete_id stardew.building.temp run tag @s remove stardew.animal.is_outside

# 删除同位置的交互实体和盔甲架
execute at @s run kill @e[type=interaction,tag=stardew.building.interaction,distance=..2]
execute at @s run kill @e[type=armor_stand,tag=stardew.building.visual,distance=..2]

# 回收ID到栈
execute if score #delete_type stardew.building.temp matches 1 run function stardew:building/core/recycle_coop_id
execute if score #delete_type stardew.building.temp matches 2 run function stardew:building/core/recycle_barn_id

# 删除自己（marker）
kill @s
