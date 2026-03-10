# stardew:mine/floor/kill_floor_entities_delayed.mcfunction
# 延迟执行：清理整层的所有实体
# 由 generate_room schedule 1t 调用，此时区块已完全加载

# 使用 storage 中的参数清理实体
function stardew:mine/floor/kill_floor_entities with storage stardew:mine gen

# 再 1 tick 后放置结构（确保清理完成）
schedule function stardew:mine/floor/generate_room_delayed 1t
