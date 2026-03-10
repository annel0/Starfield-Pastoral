# stardew:mine/floor/theme2/treasure_room/spawn_exit.mcfunction
# 在宝藏房间生成返回上层的梯子
# 参数: $(z1)
# 位置: X=14, Y=64, Z+1

$execute in stardew:mine positioned 14 65 $(z1) run function stardew:mine/ladder/spawn_exit
