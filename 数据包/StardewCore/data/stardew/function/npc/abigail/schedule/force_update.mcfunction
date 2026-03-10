# 强制更新NPC日程(跨维度安全)
# @s = npc.abigail

# 临时强制加载NPC所在区块
execute at @s run forceload add ~ ~

# 执行日程检查
execute at @s run function stardew:npc/abigail/schedule/check

# 如果需要移动,执行一步移动
execute at @s if score @s stardew.npc.path_id matches 1.. run function stardew:npc/abigail/movement/tick

# 移除临时强制加载(1秒后清理)
schedule function stardew:npc/forceload/cleanup_temp 1s replace
