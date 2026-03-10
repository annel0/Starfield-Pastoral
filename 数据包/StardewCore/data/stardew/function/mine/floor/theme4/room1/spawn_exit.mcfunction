# stardew:mine/floor/theme4/room1/spawn_exit.mcfunction
# 在 room1 生成返回上层的梯子
# 参数: $(z2)
# 位置: X=19, Y=64, Z+2

$execute in stardew:mine positioned 19 65 $(z2) run function stardew:mine/ladder/spawn_exit
