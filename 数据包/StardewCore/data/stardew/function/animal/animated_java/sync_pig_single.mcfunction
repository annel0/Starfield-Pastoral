# ================================================================
# 星露谷物语 - 同步单个 AJ 成年猪
# ================================================================
# 用途：让 root entity tp 到对应的逻辑猪位置
# @s = AJ root entity

# 保存 ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# tp 到对应的逻辑猪位置
execute as @e[type=pig,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s run tp @e[tag=aj.pig.root,tag=stardew.animal.aj_bound,limit=1,sort=nearest] ~ ~ ~ ~ 0

# 更新动画状态
function stardew:animal/animated_java/update_pig_animation