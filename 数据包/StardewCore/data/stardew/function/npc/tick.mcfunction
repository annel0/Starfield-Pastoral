# NPC系统主Tick函数
# 这是NPC系统的统一入口点，从main.mcfunction调用
# 负责调度所有NPC的tick逻辑

# 阿比盖尔NPC
execute if entity @e[tag=npc.abigail,limit=1] run function stardew:npc/abigail/tick

# 未来可以在这里添加其他NPC
# execute if entity @e[tag=npc.alex,limit=1] run function stardew:npc/alex/tick
# execute if entity @e[tag=npc.emily,limit=1] run function stardew:npc/emily/tick
