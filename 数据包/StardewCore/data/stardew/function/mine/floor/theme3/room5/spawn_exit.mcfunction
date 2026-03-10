# stardew:mine/floor/theme3/room5/spawn_exit.mcfunction
# 在 room5 生成返回上层的梯子
# 参数: $(z3)
# 位置: X=7, Y=64, Z+3

$execute in stardew:mine positioned 7 65 $(z3) run function stardew:mine/ladder/spawn_exit
