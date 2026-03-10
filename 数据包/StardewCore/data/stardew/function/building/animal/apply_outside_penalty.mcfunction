# ================================================================
# 星露谷物语 - 应用被困在外面的惩罚
# ================================================================
# 用途：如果动物在10格外且门关着，标记为被困，第二天心情-20
# 调用：从check_single_animal_outside调用，作为建筑marker执行

# 检查门是否关闭 且 动物在10格外
execute if score @s stardew.building.door_open matches 0 as @e[type=#stardew:animals,tag=stardew.animal,distance=10.1..] if score @s stardew.animal.building_id = @e[type=marker,tag=stardew.temp.check_building,limit=1] stardew.building.id run tag @s add stardew.animal.stuck_outside

# 停止回家移动
execute as @e[type=#stardew:animals,tag=stardew.animal.stuck_outside] run tag @s remove stardew.animal.going_home
execute as @e[type=#stardew:animals,tag=stardew.animal.stuck_outside] run scoreboard players set @s stardew.animal.going_home 0

# 显示消息
execute as @e[type=#stardew:animals,tag=stardew.animal.stuck_outside] run tellraw @a [{"text":"[建筑] ","color":"gold"},{"selector":"@s"},{"text":" 被困在外面了！明天心情会下降","color":"red"}]
