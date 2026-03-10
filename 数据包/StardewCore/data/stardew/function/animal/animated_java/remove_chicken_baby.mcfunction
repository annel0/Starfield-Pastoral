# ================================================================
# 移除幼年鸡 Animated Java 模型
# ================================================================
# 需要 #remove_id 已设置

# 找到对应的 root entity 并移除
execute as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #remove_id stardew.animal.temp run function animated_java:chicken_baby/remove/this
