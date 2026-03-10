# stardew:mine/floor/forceload_area.mcfunction
# 强制加载目标区块
# 参数: $(z), $(z30)

# 更新为支持最大房间尺寸 50x50
$execute in stardew:mine run forceload add 0 $(z) 50 $(z30)
