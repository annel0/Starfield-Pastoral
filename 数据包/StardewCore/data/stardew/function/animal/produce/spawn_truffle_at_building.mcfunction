# ================================================================
# 星露谷物语 - 在建筑位置生成松露
# ================================================================
# 用途：在猪所属建筑内生成松露
# 调用：从 produce_truffle.mcfunction 调用

# 根据建筑ID找到对应的建筑marker并在其位置生成松露
execute as @e[type=marker,tag=stardew.building.marker] if score @s stardew.building.id = @e[type=pig,tag=stardew.animal,limit=1,sort=nearest] stardew.animal.building at @s run function stardew:animal/produce/spawn_truffle_at_position

tellraw @a[tag=stardew.debug] ["",{"text":"[松露生成] ","color":"brown"},{"text":"在建筑ID "},{"score":{"name":"@s","objective":"stardew.animal.building"}},{"text":" 内生成松露"}]