# ================================================================
# 移除成年绵羊的 Animated Java 模型
# ================================================================
# 需要 #remove_id 已设置

# 调用 AJ 的移除函数
execute as @e[tag=aj.sheep.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #remove_id stardew.animal.temp run function animated_java:sheep/remove/this
