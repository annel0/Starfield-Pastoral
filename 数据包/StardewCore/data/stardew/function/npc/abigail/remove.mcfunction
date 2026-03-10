# 移除阿比盖尔NPC的所有实体
# 使用方法: function stardew:npc/abigail/remove

# 1. 使用Animated Java的官方remove函数
execute as @e[tag=aj.abigail.root] run function animated_java:abigail/remove/this

# 2. 杀死所有阿比盖尔相关的实体
kill @e[tag=npc.abigail]
kill @e[tag=npc.abigail.visual]
kill @e[tag=npc.abigail.interaction]

# 3. 杀死任何残留的AJ实体（备用清理）
kill @e[tag=aj.abigail.root]
kill @e[tag=aj.abigail.bone]
kill @e[tag=aj.abigail.locator]

# 4. 提示信息
tellraw @s {"text":"已移除阿比盖尔NPC","color":"green"}
