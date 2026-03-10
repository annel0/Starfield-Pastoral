# stardew:mine/floor/theme3/room5/spawn_elevator.mcfunction
# 生成电梯
# 参数: $(z3)
# 位置: X=9, Z+3

$execute in stardew:mine positioned 9 65 $(z3) run function stardew:mine/elevator/spawn_entity
