# stardew:mine/floor/theme1/room1/spawn_exit.mcfunction
# 在 theme1/room1 生成出口梯子
# 参数: $(z4)

$execute in stardew:mine positioned 3 65 $(z4) run function stardew:mine/ladder/spawn_exit
