# stardew:mine/floor/theme3/room2/spawn_exit.mcfunction
# 在 room2 生成返回上层的梯子
# 参数: $(z4)
# 位置: X=3, Y=64, Z+4

$execute in stardew:mine positioned 3 65 $(z4) run function stardew:mine/ladder/spawn_exit
