# stardew:mine/floor/theme2/room3/spawn_elevator.mcfunction
# 生成电梯（每5层）
# 参数: $(z2)
# 位置: X=3, Z+2（有 stripped_oak_log 标记）

$execute in stardew:mine positioned 3 65 $(z2) run function stardew:mine/elevator/spawn_entity
