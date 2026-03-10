# stardew:mine/floor/forceload_teleport.mcfunction
# 强制加载区块 → 传送玩家 → 卸载区块
# 参数: $(z), $(z50)
# 用于电梯传送到已存在的楼层 (不需要刷新时)

# 1. 强制加载目标区块 (覆盖 50x50 范围)
$execute in stardew:mine run forceload add 0 $(z) 50 $(z50)

# 2. 延迟 3 tick 后传送 (确保区块完全加载,从1t增加到3t防止掉虚空)
schedule function stardew:mine/floor/teleport_only_delayed 3t
