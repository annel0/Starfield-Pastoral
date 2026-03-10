# stardew:mine/floor/forceload_remove_teleport.mcfunction
# 移除电梯传送时强制加载的区块
# 参数: $(z), $(z50)

$execute in stardew:mine run forceload remove 0 $(z) 50 $(z50)
