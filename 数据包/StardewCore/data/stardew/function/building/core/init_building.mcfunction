# ================================================================
# 星露谷物语 - 初始化建筑实体
# ================================================================
# 用途：为新生成的建筑分配ID和初始化数据
# 调用：从spawn命令调用，作为marker执行
# 前提：marker已有tag标记类型（stardew.building.coop 或 stardew.building.barn）

# 分配建筑ID（鸡舍: 1-10）
# 先尝试从栈中获取ID
execute if entity @s[tag=stardew.building.coop] if entity @e[type=marker,tag=stardew.id_stack.coop,limit=1] run function stardew:building/core/pop_coop_id
# 如果栈为空，使用自增ID
execute if entity @s[tag=stardew.building.coop] unless score @s stardew.building.id matches 1.. run scoreboard players operation @s stardew.building.id = #NextCoopID stardew.building.next_coop_id
execute if entity @s[tag=stardew.building.coop] unless score @s stardew.building.id matches 1.. run scoreboard players add #NextCoopID stardew.building.next_coop_id 1
execute if entity @s[tag=stardew.building.coop] run scoreboard players set @s stardew.building.type 1

# 分配建筑ID（畜棚: 11-20）
execute if entity @s[tag=stardew.building.barn] if entity @e[type=marker,tag=stardew.id_stack.barn,limit=1] run function stardew:building/core/pop_barn_id
execute if entity @s[tag=stardew.building.barn] unless score @s stardew.building.id matches 11.. run scoreboard players operation @s stardew.building.id = #NextBarnID stardew.building.next_barn_id
execute if entity @s[tag=stardew.building.barn] unless score @s stardew.building.id matches 11.. run scoreboard players add #NextBarnID stardew.building.next_barn_id 1
execute if entity @s[tag=stardew.building.barn] run scoreboard players set @s stardew.building.type 2

# 设置等级（默认1级）
scoreboard players set @s stardew.building.tier 1

# 根据等级设置容量（1级=4只）
scoreboard players set @s stardew.building.capacity 4

# 初始化其他数据
scoreboard players set @s stardew.building.animal_count 0
scoreboard players set @s stardew.building.door_open 1

# 同步ID到同位置的交互实体和盔甲架
execute at @s run scoreboard players operation @e[tag=stardew.building.interaction,distance=..1] stardew.building.id = @s stardew.building.id
execute at @s run scoreboard players operation @e[tag=stardew.building.visual,distance=..1] stardew.building.id = @s stardew.building.id

# 同步类型标签
execute if entity @s[tag=stardew.building.coop] at @s run tag @e[tag=stardew.building.interaction,distance=..1] add stardew.building.coop
execute if entity @s[tag=stardew.building.barn] at @s run tag @e[tag=stardew.building.interaction,distance=..1] add stardew.building.barn

# 移除new标签
tag @s remove stardew.building.new
execute at @s run tag @e[tag=stardew.building.new,distance=..1] remove stardew.building.new
