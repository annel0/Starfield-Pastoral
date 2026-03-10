# stardew:mine/floor/theme4/room1/spawn_elevator.mcfunction
# 生成电梯
# 参数: $(z3)
# 位置: X=20, Z+3

$execute in stardew:mine positioned 20 65 $(z3) run function stardew:mine/elevator/spawn_entity
