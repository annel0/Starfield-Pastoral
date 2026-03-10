# ================================================================
# 星露谷物语 - Animated Java 幼年绵羊召唤包装
# ================================================================
# 用途：召唤 AJ 幼年绵羊模型并初始化
# @s = 逻辑绵羊实体（隐形的 sheep）

# 召唤 AJ 模型
# 使用宏设置初始动画为 idle
function animated_java:sheep_baby/summon {args:{animation:'idle'}}

# 为 root entity 绑定动物 ID
execute as @e[tag=aj.sheep_baby.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_sheep_baby_id
