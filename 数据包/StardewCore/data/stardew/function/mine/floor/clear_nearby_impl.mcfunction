# stardew:mine/floor/clear_nearby_impl.mcfunction
# 实际清理附近的旧实体
# 执行者: 玩家 (@s)
# 执行位置: 玩家当前位置

# 清理50格范围内的旧实体（新生成的实体有不同标记所以不会被误杀）
# 实际上新旧实体标签一样，所以这会同时杀死新的...

# 算了，换个方法 - 只杀死"多余"的实体
# 如果同一位置有多个相同实体，杀死除了最新的以外的

# 最简单的方法：用 limit 和 sort=oldest 杀死旧的
# 石头交互体
execute as @e[tag=sd_stone,distance=..50,sort=furthest] unless entity @s[tag=sd_new_spawned] run kill @s
# 石头显示
execute as @e[tag=sd_stone_display,distance=..50,sort=furthest] unless entity @s[tag=sd_new_spawned] run kill @s

# 这不对... 让我直接统计并清理多余的
# 获取当前应有的石头数（sd_mine_stones）
# 如果实际石头比这个多，杀掉多余的

# 统计当前石头交互体数量
execute store result score #actual_stones sd_mine_temp if entity @e[tag=sd_stone,distance=..50]

# 如果实际数量大于预期，说明有残留，全部杀掉新生成会处理
# 但问题是我们不知道哪些是新的...

# 直接暴力：杀掉所有不在新石头列表里的
# 但没有这个机制...

# 最终方案：直接杀掉所有，让 spawn_stones 重新生成
# 不对，spawn_stones 已经执行完了...

# 此方法不可行，需要其他解决方案
