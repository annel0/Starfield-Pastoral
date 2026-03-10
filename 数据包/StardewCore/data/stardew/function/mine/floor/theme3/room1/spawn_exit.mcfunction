# stardew:mine/floor/theme3/room1/spawn_exit.mcfunction
# 在 room1 生成返回上层的梯子
# 参数: $(z1)
# 位置: X=12, Y=64, Z+1

$execute in stardew:mine positioned 12 65 $(z1) run function stardew:mine/ladder/spawn_exit
