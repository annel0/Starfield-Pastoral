# ================================================================
# 星露谷物语 - 检查鸡蛋实体
# ================================================================
# 用途：显示当前世界中所有鸡蛋实体的信息
# 调用：手动执行 /function stardew:animal/debug/check_egg_entities

# 统计鸡蛋实体数量
execute store result score #egg_visual_count stardew.animal.temp if entity @e[type=item_display,tag=stardew.egg.visual]
execute store result score #egg_interaction_count stardew.animal.temp if entity @e[type=interaction,tag=stardew.egg.interaction]

tellraw @a [{"text":"==================== 鸡蛋实体统计 ====================","color":"gold"}]
tellraw @a [{"text":"视觉实体数量: ","color":"yellow"},{"score":{"name":"#egg_visual_count","objective":"stardew.animal.temp"},"color":"green"}]
tellraw @a [{"text":"交互实体数量: ","color":"yellow"},{"score":{"name":"#egg_interaction_count","objective":"stardew.animal.temp"},"color":"green"}]

# 显示每个交互实体的CMD
execute as @e[type=interaction,tag=stardew.egg.interaction] run tellraw @a [{"text":"  交互体 | CMD: ","color":"aqua"},{"score":{"name":"@s","objective":"stardew.item.cmd"},"color":"white"}]

# 如果没有鸡蛋实体，提示
execute if score #egg_visual_count stardew.animal.temp matches 0 run tellraw @a [{"text":"⚠ 没有找到鸡蛋实体！","color":"red"}]

tellraw @a [{"text":"===================================================","color":"gold"}]
