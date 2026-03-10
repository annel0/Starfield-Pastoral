# ================================================================
# 强制修复最近的兔子模型
# ================================================================

tellraw @a [{"text":"[兔子修复] 开始修复最近的兔子...","color":"yellow"}]

# 找到最近的兔子
execute as @e[type=chicken,tag=stardew.animal,limit=1,sort=nearest] if score @s stardew.animal.type matches 103 run tag @s add temp.rebuild_rabbit

# 检查是否找到
execute unless entity @e[tag=temp.rebuild_rabbit] run tellraw @a [{"text":"❌ 未找到兔子！","color":"red"}]
execute unless entity @e[tag=temp.rebuild_rabbit] run return 0

# 保存ID
execute as @e[tag=temp.rebuild_rabbit,limit=1] run scoreboard players operation #rebuild_id stardew.animal.temp = @s stardew.animal.id

tellraw @a [{"text":"[兔子修复] 兔子ID: ","color":"yellow"},{"score":{"name":"#rebuild_id","objective":"stardew.animal.temp"},"color":"white"}]

# 移除所有旧模型
tellraw @a [{"text":"[兔子修复] 移除旧模型...","color":"yellow"}]

# 移除成年兔模型
execute as @e[tag=aj.rabbit.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #rebuild_id stardew.animal.temp run function animated_java:rabbit/remove/this

# 移除幼年兔模型
execute as @e[tag=aj.rabbit_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.id = #rebuild_id stardew.animal.temp if score @s stardew.animal.type matches 103 run function animated_java:rabbit_baby/remove/this

# 等待一tick再生成新模型
schedule function stardew:debug/rebuild_rabbit_step2 1t
