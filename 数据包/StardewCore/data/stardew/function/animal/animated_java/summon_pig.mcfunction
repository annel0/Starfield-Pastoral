# ================================================================
# 星露谷物语 - 召唤成年猪AJ模型
# ================================================================
# @s = 猪逻辑实体
# 执行位置：猪的位置
# ================================================================

# 召唤成年猪AJ模型
function animated_java:pig/summon {args:{animation:'idle'}}

# 绑定ID
execute as @e[tag=aj.pig.root,tag=!stardew.animal.aj_bound,distance=..2,limit=1,sort=nearest] run function stardew:animal/animated_java/bind_pig_id

tellraw @a[tag=stardew.debug] ["",{"text":"[AJ猪] ","color":"light_purple"},{"text":"已召唤成年猪视觉模型","color":"white"}]