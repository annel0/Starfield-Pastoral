# ================================================================
# 星露谷物语 - 同步单个 AJ 成年山羊
# ================================================================
# 用途：让 root entity tp 到对应的逻辑山羊位置
# @s = AJ root entity

# 保存 ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# tp 到对应的逻辑山羊位置（山羊使用sheep实体，type=202）
execute as @e[type=sheep,tag=stardew.animal,scores={stardew.animal.type=202}] if score @s stardew.animal.id = #sync_id stardew.animal.temp at @s run tp @e[tag=aj.goat.root,tag=stardew.animal.aj_bound,limit=1,sort=nearest] ~ ~ ~ ~ 0

# 更新动画状态
function stardew:animal/animated_java/update_goat_animation
