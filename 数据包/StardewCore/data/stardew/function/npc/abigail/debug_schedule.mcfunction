# Debug: 显示阿比盖尔的日程状态

tellraw @s ["",{"text":"=== 阿比盖尔日程调试 ===","color":"gold","bold":true}]
tellraw @s ["",{"text":"当前时间: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_time"},"color":"white"}]
tellraw @s ["",{"text":"Tick计数器: ","color":"yellow"},{"score":{"name":"Global","objective":"sd_tick_counter"},"color":"white"}]

# 检查阿比盖尔实体是否存在
execute unless entity @e[tag=npc.abigail] run tellraw @s {"text":"错误: 找不到阿比盖尔实体!","color":"red"}
execute if entity @e[tag=npc.abigail] run tellraw @s {"text":"✓ 找到阿比盖尔实体","color":"green"}

# 显示scoreboard值(如果不存在会显示为空)
execute as @e[tag=npc.abigail,limit=1] store result score #debug_schedule stardew.temp run scoreboard players get @s stardew.npc.schedule
execute as @e[tag=npc.abigail,limit=1] store result score #debug_path_id stardew.temp run scoreboard players get @s stardew.npc.path_id
execute as @e[tag=npc.abigail,limit=1] store result score #debug_path_index stardew.temp run scoreboard players get @s stardew.npc.path_index

tellraw @s ["",{"text":"当前状态值: ","color":"yellow"},{"score":{"name":"#debug_schedule","objective":"stardew.temp"},"color":"white"}]
tellraw @s ["",{"text":"路径ID: ","color":"yellow"},{"score":{"name":"#debug_path_id","objective":"stardew.temp"},"color":"white"}]
tellraw @s ["",{"text":"路径索引: ","color":"yellow"},{"score":{"name":"#debug_path_index","objective":"stardew.temp"},"color":"white"}]

# 手动触发一次schedule check
tellraw @s {"text":"手动触发日程检查...","color":"aqua"}
execute as @e[tag=npc.abigail] at @s run function stardew:npc/abigail/schedule/check
