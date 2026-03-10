# stardew:mine/floor/theme1/room1/spawn_elevator.mcfunction
# 在 theme1/room1 生成电梯
# 参数: $(z4)

$execute in stardew:mine positioned 4 65 $(z4) run function stardew:mine/elevator/spawn_entity
