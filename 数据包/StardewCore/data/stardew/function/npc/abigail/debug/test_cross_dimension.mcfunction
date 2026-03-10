# 测试跨维度NPC系统
# 显示当前NPC状态和时间信息

tellraw @s {"text":"========== 跨维度NPC系统测试 ==========","color":"gold","bold":true}
tellraw @s ""

# 当前时间
tellraw @s [{"text":"当前时间: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_time"},"color":"white"},{"text":" (星期","color":"gray"},{"score":{"name":"Global","objective":"sd_day_of_week"},"color":"white"},{"text":")","color":"gray"}]

# NPC状态
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"当前日程: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.schedule"},"color":"white"}]
execute as @e[tag=npc.abigail,limit=1] run tellraw @a [{"text":"目标日程: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.target_schedule"},"color":"aqua"}]
execute as @e[tag=npc.abigail,limit=1] at @s run tellraw @a [{"text":"当前位置: ","color":"yellow"},{"nbt":"Pos","entity":"@s","color":"white"}]

tellraw @s ""

# 玩家维度
execute if predicate stardew:in_overworld run tellraw @s [{"text":"你在: ","color":"yellow"},{"text":"主世界","color":"green"}]
execute unless predicate stardew:in_overworld run tellraw @s [{"text":"你在: ","color":"yellow"},{"text":"其他维度","color":"red"}]

tellraw @s ""

# 测试说明
tellraw @s [{"text":"测试步骤:","color":"aqua","bold":true}]
tellraw @s [{"text":"1. ","color":"gray"},{"text":"记录当前时间和NPC位置","color":"white"}]
tellraw @s [{"text":"2. ","color":"gray"},{"text":"进入建筑内部","color":"white"}]
tellraw @s [{"text":"3. ","color":"gray"},{"text":"等待时间变化(触发日程切换)","color":"white"}]
tellraw @s [{"text":"4. ","color":"gray"},{"text":"返回主世界","color":"white"}]
tellraw @s [{"text":"5. ","color":"gray"},{"text":"NPC应该瞬移到新位置","color":"white"}]

tellraw @s ""
tellraw @s {"text":"===================================","color":"gold","bold":true}
