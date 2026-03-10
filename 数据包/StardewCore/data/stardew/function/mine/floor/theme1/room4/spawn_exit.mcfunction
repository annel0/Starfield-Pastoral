# stardew:mine/floor/theme1/room4/spawn_exit.mcfunction
# 在 theme1/room4 生成出口梯子
# 参数: $(z2)

$execute in stardew:mine positioned 19 65 $(z2) run function stardew:mine/ladder/spawn_exit
