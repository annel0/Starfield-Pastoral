# ================================================================
# 召唤成年兔 Animated Java 模型
# ================================================================
# @s = 逻辑兔（静音鸡实体）

# 在逻辑实体位置召唤 AJ 模型
execute at @s run function animated_java:rabbit/summon {args:{animation:'idle'}}

# 绑定 ID
execute at @s as @e[tag=aj.rabbit.root,tag=!stardew.animal.aj_bound,sort=nearest,limit=1] run function stardew:animal/animated_java/bind_rabbit_id
