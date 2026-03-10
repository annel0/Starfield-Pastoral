# stardew:mine/floor/theme1/room5/spawn_elevator.mcfunction
# 在 theme1/room5 生成电梯
# 参数: $(z3)

$execute in stardew:mine positioned 25 65 $(z3) run function stardew:mine/elevator/spawn_entity
