# stardew:mine/floor/theme3/treasure_room/spawn_pit.mcfunction
# 在宝藏房间生成固定的通往下层的坑
# 参数: $(z13)
# 位置: X=9, Y=64, Z+13
# 特点: 不需要挖矿就可以直接进入下一层

$execute in stardew:mine positioned 9 65 $(z13) run function stardew:mine/ladder/spawn_pit
