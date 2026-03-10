# stardew:mine/floor/theme1/treasure_room/teleport.mcfunction
# 传送玩家到宝藏房间出生点
# 参数: $(z1) - 从 tp.z1 传入
# 位置: X=14, Y=65, Z+1, 面朝南 (180度)

$execute in stardew:mine run tp @s 14 65 $(z1) 180 0
