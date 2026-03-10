# ================================================================
# 星露谷物语 - 在建筑内生成鸡蛋
# ================================================================
# 用途：在鸡所属的建筑中心附近生成鸡蛋
# 调用：从 spawn_egg_entity.mcfunction 调用

# 保存当前鸡的 building ID 到临时变量
scoreboard players operation #target_building stardew.temp = @s stardew.animal.building

# 找到对应的建筑实体并在其位置生成鸡蛋
execute as @e[type=interaction,tag=stardew.building] if score @s stardew.building.id = #target_building stardew.temp at @s run function stardew:animal/produce/spawn_egg_at_position
