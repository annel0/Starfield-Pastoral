# 更新NPC的目标位置(快照更新)
# @s = npc.abigail
# 这个函数会让NPC开始沿路径移动或直接传送到目的地

# 执行日程检查,这会设置target_schedule和path_id
function stardew:npc/abigail/schedule/check

# 如果设置了新的目标日程:
# - 如果设置了path_id: 让NPC沿路径走
# - 如果没有path_id: 直接瞬移到目的地
execute if score @s stardew.npc.target_schedule matches 1.. unless score @s stardew.npc.path_id matches 1.. run function stardew:npc/abigail/schedule/teleport_to_target
