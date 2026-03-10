# ================================================================
# 星露谷物语 - 同步单个 AJ 幼年牛
# ================================================================
# 用途：让 root entity tp 到对应的逻辑牛位置
# @s = AJ root entity

# 保存 ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# tp 到对应的逻辑牛位置
execute as @e[type=cow,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s run tp @e[tag=aj.cow_baby.root,tag=stardew.animal.aj_bound,limit=1,sort=nearest] ~ ~ ~ ~ 0

# 更新动画状态
function stardew:animal/animated_java/update_cow_baby_animation

# 检测是否需要成长转换
scoreboard players set #need_grow stardew.animal.temp 0
execute as @e[type=cow,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp if score @s stardew.animal.age matches 5.. run scoreboard players set #need_grow stardew.animal.temp 1

# 如果需要成长，调用转换函数
execute if score #need_grow stardew.animal.temp matches 1 run scoreboard players operation #check_id stardew.animal.temp = #sync_id stardew.animal.temp
execute if score #need_grow stardew.animal.temp matches 1 as @e[type=cow,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run function stardew:animal/animated_java/check_cow_growth
