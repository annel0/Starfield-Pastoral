# stardew:mine/floor/theme3/treasure_room/spawn_elevator.mcfunction
# 生成电梯
# 参数: $(z1)
# 位置: X=17, Z+1

$execute in stardew:mine positioned 17 65 $(z1) run function stardew:mine/elevator/spawn_entity
