# ================================================================
# 强制让附近的鸭子长大
# ================================================================
# 用途：手动触发最近的鸭子成长（用于测试）

# 找到最近的鸭子
execute as @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] if score @s stardew.animal.type matches 102 run tag @s add temp.force_grow

# 设置年龄为5天（如果不足）
execute as @e[tag=temp.force_grow] if score @s stardew.animal.age matches ..4 run scoreboard players set @s stardew.animal.age 5

# 设置检查ID
execute as @e[tag=temp.force_grow] run scoreboard players operation #check_id stardew.animal.temp = @s stardew.animal.id

# 执行成长检查
execute as @e[tag=temp.force_grow] run function stardew:animal/animated_java/check_duck_growth

# 清理标签
tag @e remove temp.force_grow

tellraw @a [{"text":"[动物系统] ","color":"green"},{"text":"已强制触发鸭子成长检查","color":"yellow"}]
