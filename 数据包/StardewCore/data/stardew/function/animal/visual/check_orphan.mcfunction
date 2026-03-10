# ================================================================
# 星露谷物语 - 检查视觉实体是否为孤儿
# ================================================================
# @s = 视觉实体

# 保存ID
scoreboard players operation #check_id stardew.animal.temp = @s stardew.animal.id

# 如果找到对应的逻辑实体，移除孤儿标签
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = #check_id stardew.animal.temp run tag @e[type=item_display,tag=stardew.animal.visual,limit=1,sort=nearest] remove stardew.temp.orphan
