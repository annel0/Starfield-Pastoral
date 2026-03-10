# ================================================================
# 星露谷物语 - Animated Java 牛召唤包装
# ================================================================
# 用途：召唤 AJ 牛模型并初始化
# @s = 逻辑牛实体（隐形的 cow）

# 召唤 AJ 模型
# 使用宏设置初始动画为 idle
function animated_java:cow/summon {args:{animation:'idle'}}

# 为 root entity 绑定动物 ID
execute as @e[tag=aj.cow.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_cow_id
