# NPC交互处理
# 当玩家点击interaction实体时调用

# 获取NPC ID
scoreboard players operation #temp stardew_npc_id = @s stardew_npc_id

# 根据NPC ID调用对应的交互函数
execute if score #temp stardew_npc_id matches 1 run function stardew:npc/alex/interact
execute if score #temp stardew_npc_id matches 2 run function stardew:npc/abigail/interact
execute if score #temp stardew_npc_id matches 3 run function stardew:npc/lewis/interact

# 清理临时分数
scoreboard players reset #temp stardew_npc_id

# 清除interaction的攻击数据
data remove entity @s attack
data remove entity @s interaction