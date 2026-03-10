# NPC诊断工具 - 检查阿比盖尔的状态
# 使用方法: /function stardew:npc/abigail/debug/diagnose

tellraw @s {"text":"========== 阿比盖尔NPC诊断 ==========","color":"gold","bold":true}
tellraw @s ""

# 1. 检查核心村民实体
execute if entity @e[tag=npc.abigail,limit=1] run tellraw @s [{"text":"✓ ","color":"green"},{"text":"核心村民实体存在","color":"white"}]
execute unless entity @e[tag=npc.abigail,limit=1] run tellraw @s [{"text":"✗ ","color":"red"},{"text":"核心村民实体不存在!","color":"white"}]

# 2. 检查AJ视觉模型
execute if entity @e[tag=npc.abigail.visual,limit=1] run tellraw @s [{"text":"✓ ","color":"green"},{"text":"Animated Java视觉模型存在","color":"white"}]
execute unless entity @e[tag=npc.abigail.visual,limit=1] run tellraw @s [{"text":"✗ ","color":"red"},{"text":"Animated Java视觉模型不存在!","color":"white"}]

# 3. 检查Interaction实体
execute if entity @e[tag=npc.abigail.interaction,limit=1] run tellraw @s [{"text":"✓ ","color":"green"},{"text":"交互实体存在","color":"white"}]
execute unless entity @e[tag=npc.abigail.interaction,limit=1] run tellraw @s [{"text":"✗ ","color":"red"},{"text":"交互实体不存在!","color":"white"}]

tellraw @s ""

# 4. 检查计分板数值
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"日程状态: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.schedule"},"color":"white"}]
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"路径ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.path_id"},"color":"white"}]
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"动画状态: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.animation"},"color":"white"}]
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"NPC ID: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.id"},"color":"white"}]

tellraw @s ""

# 5. 检查位置
execute as @e[tag=npc.abigail,limit=1] at @s run tellraw @a [{"text":"位置: ","color":"yellow"},{"nbt":"Pos","entity":"@s","color":"white"}]
execute as @e[tag=npc.abigail,limit=1] at @s run tellraw @a [{"text":"朝向: ","color":"yellow"},{"nbt":"Rotation","entity":"@s","color":"white"}]

tellraw @s ""

# 6. 检查时间系统
tellraw @s [{"text":"当前时间: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_time"},"color":"white"}]
tellraw @s [{"text":"星期几: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_day_of_week"},"color":"white"}]
tellraw @s [{"text":"Tick计数器: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_tick_counter"},"color":"white"}]

tellraw @s ""
tellraw @s {"text":"===================================","color":"gold","bold":true}
