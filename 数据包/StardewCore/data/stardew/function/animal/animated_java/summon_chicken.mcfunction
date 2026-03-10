# ================================================================
# 星露谷物语 - Animated Java 鸡召唤包装
# ================================================================
# 用途：召唤 AJ 鸡模型并初始化
# @s = 逻辑鸡实体（隐形的 chicken）

# 召唤 AJ 模型
# 使用宏设置初始动画为 idle
function animated_java:chicken/summon {args:{animation:'idle'}}

# 为 root entity 绑定动物 ID
execute as @e[tag=aj.chicken.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_chicken_id
