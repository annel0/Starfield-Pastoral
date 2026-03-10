# stardew:mine/floor/theme2/room3/teleport.mcfunction
# 传送玩家到 theme2/room3 的出生点（面朝正东）
# 参数: $(z4)

$execute in stardew:mine run tp @s 3 65 $(z4) 90 0
