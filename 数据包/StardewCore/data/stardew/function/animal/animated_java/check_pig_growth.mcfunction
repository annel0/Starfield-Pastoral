# ================================================================
# 星露谷物语 - 检查猪成长
# ================================================================
# @s = 幼年猪逻辑实体（需要成长）
# ================================================================

# 移除幼年猪的AJ模型
execute as @e[tag=aj.pig_baby.root] if score @s stardew.animal.id = @e[type=pig,tag=stardew.animal,limit=1,sort=nearest] stardew.animal.id run function animated_java:pig_baby/remove

# 生成成年猪AJ模型
function stardew:animal/animated_java/summon_pig

tellraw @a[tag=stardew.debug] ["",{"text":"[猪成长] ","color":"light_purple"},{"text":"猪 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 成长为成年","color":"white"}]