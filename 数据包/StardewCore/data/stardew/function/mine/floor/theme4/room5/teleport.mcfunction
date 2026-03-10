# stardew:mine/floor/theme4/room5/teleport.mcfunction
# 传送玩家到梯子位置
# 参数: $(z2)
# 位置: X=18, Z+2

$execute in stardew:mine run tp @s 18.5 65 $(z2) 180 0
