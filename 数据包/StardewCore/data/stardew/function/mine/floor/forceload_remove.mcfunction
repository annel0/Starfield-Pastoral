# stardew:mine/floor/forceload_remove.mcfunction
# 移除强制加载的区块
# 参数: $(z), $(z30)

# 更新为支持最大房间尺寸 50x50
$execute in stardew:mine run forceload remove 0 $(z) 50 $(z30)
