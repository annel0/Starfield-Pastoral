# ================================================================
# 星露谷物语 - 在建筑生成已绑定的鸡 (Debug)
# ================================================================
# 用途：在最近的建筑生成一只已绑定的鸡
# 调用：/function stardew:building/debug/spawn_chicken_in_building

# 找到最近的建筑
tag @e[type=marker,tag=stardew.building] remove stardew.temp.target_building
tag @e[type=marker,tag=stardew.building,distance=..30,limit=1,sort=nearest] add stardew.temp.target_building

# 检查是否找到建筑
execute unless entity @e[tag=stardew.temp.target_building] run tellraw @s [{"text":"[错误] ","color":"red"},{"text":"附近30格内没有建筑！"}]
execute unless entity @e[tag=stardew.temp.target_building] run return 0

# 在建筑位置生成鸡
execute at @e[tag=stardew.temp.target_building,limit=1] run summon chicken ~1 ~ ~1 {Tags:["stardew.animal","stardew.animal.new"],CustomName:'{"text":"测试鸡","color":"yellow"}'}

# 初始化鸡的数据
execute as @e[tag=stardew.animal.new,limit=1] run function stardew:building/debug/init_animal

# 清除标签
tag @e remove stardew.temp.target_building
