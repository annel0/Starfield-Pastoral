# stardew:mine/floor/theme2/room2/spawn_elevator.mcfunction
# 生成电梯（每5层）
# 参数: $(z3)
# 位置: X=8, Z+3

$execute in stardew:mine positioned 8 65 $(z3) run function stardew:mine/elevator/spawn_entity
