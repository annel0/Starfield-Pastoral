# stardew:mine/floor/theme3/room2/spawn_elevator.mcfunction
# 生成电梯
# 参数: $(z2)
# 位置: X=3, Z+2

$execute in stardew:mine positioned 3 65 $(z2) run function stardew:mine/elevator/spawn_entity
