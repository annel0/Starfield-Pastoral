# ================================================================
# 星露谷物语 - 移除成年山羊模型
# ================================================================
# 需要 #remove_id 已设置
# ================================================================

# 移除对应的 AJ 山羊模型
execute as @e[tag=aj.goat.root] if score @s stardew.animal.id = #remove_id stardew.animal.temp run function animated_java:goat/remove/this
