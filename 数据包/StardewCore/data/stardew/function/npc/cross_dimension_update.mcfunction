# NPC跨维度日程更新系统 - 快照同步版本
# 在时间系统中每分钟调用一次
# 不使用强制加载,而是直接更新NPC的日程状态(快照)
# 当玩家回到主世界时,NPC会瞬间传送到应该在的位置

# 更新阿比盖尔的日程状态(不移动,只更新应该在哪里)
execute as @e[tag=npc.abigail] run function stardew:npc/abigail/schedule/update_target_location
