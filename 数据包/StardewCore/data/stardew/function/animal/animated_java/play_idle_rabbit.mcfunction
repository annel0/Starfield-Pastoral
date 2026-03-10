# ================================================================
# 星露谷物语 - 播放兔子 idle 动画
# ================================================================
# @s = AJ root entity (rabbit/rabbit_baby)

# 暂停所有动画
execute if entity @s[tag=aj.rabbit.root] run function animated_java:rabbit/animations/pause_all
execute if entity @s[tag=aj.rabbit_baby.root] run function animated_java:rabbit_baby/animations/pause_all

# 播放 idle 动画
execute if entity @s[tag=aj.rabbit.root] run function animated_java:rabbit/animations/idle/play
execute if entity @s[tag=aj.rabbit_baby.root] run function animated_java:rabbit_baby/animations/idle/play
