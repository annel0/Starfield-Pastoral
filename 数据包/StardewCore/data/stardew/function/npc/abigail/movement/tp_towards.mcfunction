# 朝向目标并传送（宏函数 - 多NPC并发安全）
# @s = 任何npc实体（villager）
# 朝向由tick.mcfunction统一控制，这里只负责移动

# 移动villager和visual（保持相对位置）
tp @s ^ ^ ^0.15
execute at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] ~ ~ ~ ~ ~
execute at @s run tp @e[tag=npc.abigail.interaction,limit=1,sort=nearest] ~ ~ ~


