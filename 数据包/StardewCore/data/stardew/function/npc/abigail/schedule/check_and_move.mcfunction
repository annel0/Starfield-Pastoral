# 日程检查并移动一步(跨维度安全版本)
# @s = npc.abigail
# 这个函数会在时间calc时每分钟调用一次

# 1. 临时强制加载当前区块
forceload add ~ ~

# 2. 执行日程检查
function stardew:npc/abigail/schedule/check

# 3. 如果正在移动路径,执行移动逻辑
execute if score @s stardew.npc.path_id matches 1.. run function stardew:npc/abigail/movement/tick

# 4. 延迟移除强制加载(2秒后)
schedule function stardew:npc/forceload/cleanup_abigail 2s replace
