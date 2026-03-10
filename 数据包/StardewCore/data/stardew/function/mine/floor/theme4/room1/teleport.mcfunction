# stardew:mine/floor/theme4/room1/teleport.mcfunction
# 传送玩家到 theme4 room1 的梯子位置
# 参数: $(z2)
# 面朝南 (yaw=180)

$execute in stardew:mine run tp @s 19 65 $(z2) 180 0
