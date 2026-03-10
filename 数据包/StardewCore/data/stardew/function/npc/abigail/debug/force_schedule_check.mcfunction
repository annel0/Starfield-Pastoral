# 强制触发阿比盖尔的日程检查
# 用于调试 - 立即触发schedule/check

execute as @e[tag=npc.abigail,limit=1] at @s run function stardew:npc/abigail/schedule/check

tellraw @s [{"text":"[调试] ","color":"yellow"},{"text":"已强制触发日程检查","color":"white"}]
