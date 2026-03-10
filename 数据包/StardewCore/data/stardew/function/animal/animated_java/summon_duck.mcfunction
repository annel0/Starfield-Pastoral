# ================================================================
# 星露谷物语 - Animated Java 鸭子召唤包装
# ================================================================
# 用途：召唤 AJ 鸭子模型并初始化
# @s = 逻辑鸭实体（隐形的 chicken）

# 召唤 AJ 模型
# 使用宏设置初始动画为 idle
function animated_java:duck/summon {args:{animation:'idle'}}

# 为 root entity 绑定动物 ID
execute as @e[tag=aj.duck.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_duck_id
