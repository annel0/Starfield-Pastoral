# stardew:mine/floor/theme4/room4/teleport.mcfunction
# 传送玩家到 theme4 room4 的梯子位置
# 参数: $(z3)
# 面朝南 (yaw=180)

$execute in stardew:mine run tp @s 7 65 $(z3) 180 0
