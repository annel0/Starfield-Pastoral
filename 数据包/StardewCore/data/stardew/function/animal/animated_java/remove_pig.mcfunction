# ================================================================
# 星露谷物语 - 移除 AJ 猪
# ================================================================
# 用途：清理 AJ 模型
# 前提：#remove_id 已设置为要移除的动物 ID

# 找到对应的 root entity 并移除
execute as @e[tag=aj.pig.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #remove_id stardew.animal.temp run function animated_java:pig/remove/this
