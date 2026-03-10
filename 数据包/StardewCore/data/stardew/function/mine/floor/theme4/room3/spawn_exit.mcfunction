# stardew:mine/floor/theme4/room3/spawn_exit.mcfunction
# 在 room3 生成返回上层的梯子
# 参数: $(z3)
# 位置: X=5, Y=64, Z+3

$execute in stardew:mine positioned 5 65 $(z3) run function stardew:mine/ladder/spawn_exit
