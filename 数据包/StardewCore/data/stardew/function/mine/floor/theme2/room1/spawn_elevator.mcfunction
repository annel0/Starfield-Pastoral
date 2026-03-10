# stardew:mine/floor/theme2/room1/spawn_elevator.mcfunction
# 生成电梯（每5层）
# 参数: $(z3)
# 位置: X=7, Z+3（有 stripped_oak_log 标记）

$execute in stardew:mine positioned 7 65 $(z3) run function stardew:mine/elevator/spawn_entity
