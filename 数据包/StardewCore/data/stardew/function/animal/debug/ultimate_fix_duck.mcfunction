# ================================================================
# 终极修复 - 强制重建鸭子模型
# ================================================================
# 用途：无论当前状态如何，都强制重建正确的模型

# 找到最近的鸭子
execute as @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] if score @s stardew.animal.type matches 102 run tag @s add temp.rebuild

execute unless entity @e[tag=temp.rebuild] run tellraw @a [{"text":"[错误] ","color":"red"},{"text":"附近没有找到鸭子","color":"white"}]
execute unless entity @e[tag=temp.rebuild] run return 0

# 获取鸭子信息
execute as @e[tag=temp.rebuild] run scoreboard players operation #rebuild_id stardew.animal.temp = @s stardew.animal.id
execute as @e[tag=temp.rebuild] run scoreboard players operation #rebuild_age stardew.animal.temp = @s stardew.animal.age

tellraw @a [{"text":"[修复] ","color":"green"},{"text":"开始重建鸭子模型...","color":"yellow"}]

# 1. 移除所有旧模型（不管是什么）
# 移除幼年鸭模型（chicken_baby，type=102）
execute as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #rebuild_id stardew.animal.temp if score @s stardew.animal.type matches 102 run function animated_java:chicken_baby/remove/this
tellraw @a [{"text":"  - 清理幼年鸭模型","color":"gray"}]

# 移除成年鸭模型
execute as @e[tag=aj.duck.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #rebuild_id stardew.animal.temp run function animated_java:duck/remove/this
tellraw @a [{"text":"  - 清理成年鸭模型","color":"gray"}]

# 移除旧的item_display
execute as @e[type=item_display,tag=stardew.animal.visual] if score @s stardew.animal.id = #rebuild_id stardew.animal.temp run kill @s
tellraw @a [{"text":"  - 清理旧视觉实体","color":"gray"}]

# 等待1 tick让移除生效（使用schedule）
schedule function stardew:animal/debug/rebuild_duck_step2 1t

tellraw @a [{"text":"[修复] ","color":"green"},{"text":"1秒后将生成新模型...","color":"yellow"}]
