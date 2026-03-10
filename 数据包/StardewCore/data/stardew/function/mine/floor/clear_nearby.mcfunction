# stardew:mine/floor/clear_nearby.mcfunction
# 清理玩家附近的旧矿洞实体
# 由 schedule 延迟调用，确保区块已完全加载

# 获取最近的玩家（在矿洞维度内）
execute as @a[scores={sd_mine_floor=1..}] at @s in stardew:mine run function stardew:mine/floor/clear_nearby_impl
