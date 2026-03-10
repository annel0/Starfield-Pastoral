# 检查伤害显示实体是否存在

execute if entity @e[type=text_display,tag=sd_damage_display] run tellraw @s [{"text":"[检测] 找到伤害显示实体！数量: ","color":"green"},{"selector":"@e[type=text_display,tag=sd_damage_display]"}]
execute unless entity @e[type=text_display,tag=sd_damage_display] run tellraw @s [{"text":"[检测] 没有找到任何伤害显示实体！","color":"red"}]

execute store result score #count sd_temp if entity @e[type=text_display,tag=sd_damage_display]
tellraw @s [{"text":"[检测] 实体数量: ","color":"gold"},{"score":{"name":"#count","objective":"sd_temp"},"color":"yellow"}]

execute as @e[type=text_display,tag=sd_damage_display] run tellraw @s [{"text":"[检测] 找到一个实体，标签: ","color":"aqua"},{"nbt":"Tags","entity":"@s"}]
