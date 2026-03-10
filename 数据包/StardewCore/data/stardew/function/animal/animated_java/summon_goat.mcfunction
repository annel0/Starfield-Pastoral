# ================================================================
# 星露谷物语 - 召唤成年山羊视觉实体
# ================================================================
# @s = 山羊逻辑实体
# ================================================================

# 召唤 Animated Java 山羊模型（需要宏参数指定初始动画）
function animated_java:goat/summon {args:{animation:'idle'}}

# 绑定ID到新生成的模型
execute as @e[tag=aj.goat.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_goat_id
