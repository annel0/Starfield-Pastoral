# stardew:mine/floor/theme4/room2/teleport.mcfunction
# 传送玩家到 theme4 room2 的梯子位置
# 参数: $(z4)
# 面朝正东 (yaw=90)

$execute in stardew:mine run tp @s 3 65 $(z4) 90 0
