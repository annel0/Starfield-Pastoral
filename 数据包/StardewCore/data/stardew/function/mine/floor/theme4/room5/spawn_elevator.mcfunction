# stardew:mine/floor/theme4/room5/spawn_elevator.mcfunction
# 生成电梯
# 参数: $(z2)
# 位置: X=20, Z+2

$execute in stardew:mine positioned 20 65 $(z2) run function stardew:mine/elevator/spawn_entity
