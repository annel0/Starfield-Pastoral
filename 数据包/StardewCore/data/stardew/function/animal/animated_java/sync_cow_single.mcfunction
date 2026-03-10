# ================================================================
# 星露谷物语 - 同步单个 AJ 牛
# ================================================================
# 用途：让 root entity tp 到对应的逻辑牛位置
# @s = AJ root entity

# 保存 ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# tp 到对应的逻辑牛位置
# 因为 Auto Update Rig Orientation 启用，所有子实体会自动跟随
execute as @e[type=cow,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s run tp @e[tag=aj.cow.root,tag=stardew.animal.aj_bound,limit=1,sort=nearest] ~ ~ ~ ~ 0

# 更新动画状态
function stardew:animal/animated_java/update_cow_animation
