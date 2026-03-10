# stardew:mine/floor/theme1/room3/teleport.mcfunction
# 传送玩家到 theme1/room3 出生点
# 参数: $(z3)
# 位置: X=4, Z=z+3, 面朝南 (180度)

$execute in stardew:mine run tp @s 4 65 $(z3) 180 0
