# stardew:mine/floor/generate_room_scheduled.mcfunction
# 延迟执行：先清理旧实体
# 由 generate_room.mcfunction schedule 2t 调用

# 1. 清理旧实体
function stardew:mine/floor/kill_floor_entities with storage stardew:mine gen

# 2. 再延迟 1 tick 生成，确保清理完成
schedule function stardew:mine/floor/generate_room_delayed 1t
