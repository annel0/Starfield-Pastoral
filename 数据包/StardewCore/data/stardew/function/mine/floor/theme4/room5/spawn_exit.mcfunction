# stardew:mine/floor/theme4/room5/spawn_exit.mcfunction
# 生成出口梯子
# 参数: $(z2)
# 位置: X=18, Z+2

$execute in stardew:mine positioned 18 65 $(z2) run function stardew:mine/ladder/spawn_pit
